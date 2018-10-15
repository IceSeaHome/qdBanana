package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.Manager;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.TimeTools;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manage/expTake/")
public class ExpTakeManageController extends BaseController {

    @Autowired
    private ExpTakeService takeService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("list")
    private Object list(Long timeStart, Long timeEnd, Long expBrand) {
        Long[] today = TimeTools.today();
        if (hasEmptyString(timeEnd, timeStart)) {
            timeStart = today[0];
            timeEnd = today[1];
        }

        List<ExpTakeOrder> ls = takeService.findTimeBetween(timeStart, timeEnd);

        ls = ls.stream()
            .filter(v -> expBrand == null || v.getExpId().equals(expBrand))
            .collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.put("all", formatData(ls));
        data.put("paid", formatData(ls.stream().filter(v -> v.getPaid()).collect(Collectors.toList())));
        data.put("other", formatData(ls.stream().filter(v -> !v.getPaid()).collect(Collectors.toList())));
        return success(data, null);
    }

    private JSONArray formatData(List<ExpTakeOrder> ls) {
        JSONArray arr = new JSONArray();
        for (ExpTakeOrder l : ls) {
            JSONObject obj = new JSONObject();
            obj.putAll(toJsonObject(l));
            obj.put("title", l.getExpName());
            obj.put("statusName", OrderStatusEnum.valueOf(l.getStatus()).getName());

            JSONObject infos = new JSONObject();
            infos.put("下单时间", l.getCreatedTime());
            infos.put("流水序号", l.getExpId());
            infos.put("取件姓名", l.getExpTakeName());
            //            infos.put("用户序号", l.getUserId());
            infos.put("取件手机", l.getExpTakePhone());
            infos.put("配送手机", l.getSendPhone());
            infos.put("配送地址", l.getSendAddr());
            infos.put("本单费用", String.format("%.2f", l.getTotalFee() / 100.0));
            infos.put("取件短信", l.getSmsText());
            infos.put("客户备注", l.getRemark());
            obj.put("infos", infos);
            arr.add(obj);
        }
        return arr;
    }

    //    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            takeService.updateAndSave(getSessionPersistent(Manager.class), map);
        } catch (Exception e) {
            logger.error("update expTake order failed!", e);
            return fail(e.getMessage());
        }

        return success();
    }

    @GetMapping("accept")
    public Object accept(@RequestParam Long unifiedId) {
        Asserts.check(readOnly(), "read only mananger!");
        ExpTakeOrder expTakeOrder = takeService.findById(unifiedId);
        if (expTakeOrder == null || !(OrderStatusEnum.PAIED.getCode() == expTakeOrder.getStatus())) {
            return fail("status not right!");
        }
        expTakeOrder.setStatus(OrderStatusEnum.PROCESSING.getCode());
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(expTakeOrder.getUnifiedId());
        unifiedOrder.setStatus(OrderStatusEnum.PROCESSING.getCode());
        unifiedOrderService.update(unifiedOrder);

        return success();
    }

    @GetMapping("complete")
    public Object complete(@RequestParam Long unifiedId) {
        Asserts.check(readOnly(), "read only mananger!");
        ExpTakeOrder expTakeOrder = takeService.findById(unifiedId);
        if (expTakeOrder == null || !(OrderStatusEnum.PROCESSING.getCode() == expTakeOrder.getStatus())) {
            return fail("status not right!");
        }
        expTakeOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(expTakeOrder.getUnifiedId());
        unifiedOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        unifiedOrderService.update(unifiedOrder);

        return success();
    }

    @GetMapping("cancel")
    public Object cancel(@RequestParam Long unifiedId) {
        Asserts.check(readOnly(), "read only mananger!");
        if (unifiedOrderService.cancel(unifiedId)) {
            UnifiedOrder unifiedOrder = new UnifiedOrder();
            unifiedOrder.setId(unifiedId);
            takeService.cancel(unifiedOrder);
            return success();
        }
        return fail("退款失败");
    }
}
