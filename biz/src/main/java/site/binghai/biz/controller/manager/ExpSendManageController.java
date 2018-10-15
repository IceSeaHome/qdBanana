package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.util.Asserts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.service.ExpChargeService;
import site.binghai.biz.service.ExpSendService;
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
@RequestMapping("/manage/expSend/")
public class ExpSendManageController extends BaseController {

    @Autowired
    private ExpChargeService expChargeService;
    @Autowired
    private ExpSendService sendService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("list")
    private Object list(Long timeStart, Long timeEnd, Long expBrand) {
        Long[] today = TimeTools.today();
        if (hasEmptyString(timeEnd, timeStart)) {
            timeStart = today[0];
            timeEnd = today[1];
        }

        List<ExpSendOrder> ls = sendService.findTimeBetween(timeStart, timeEnd);

        ls = ls.stream()
            .filter(v -> expBrand == null || v.getExpId().equals(expBrand))
            .collect(Collectors.toList());

        JSONObject data = new JSONObject();
        data.put("all", formatData(ls));
        data.put("paid", formatData(ls.stream().filter(v -> v.getPaid()).collect(Collectors.toList())));
        data.put("other", formatData(ls.stream().filter(v -> !v.getPaid()).collect(Collectors.toList())));
        return success(data, null);
    }

    private Object formatData(List<ExpSendOrder> ls) {
        JSONArray array = new JSONArray();
        for (ExpSendOrder l : ls) {
            JSONObject obj = new JSONObject();
            obj.putAll(toJsonObject(l));
            obj.put("title", l.getExpName());
            obj.put("statusName", OrderStatusEnum.valueOf(l.getStatus()).getName());
            JSONObject infos = new JSONObject();

            infos.put("下单时间", l.getCreatedTime());
            infos.put("流水序号", l.getId());
            //            infos.put("用户序号",l.getUserId());
            infos.put("取件手机", l.getFetchPhone());
            infos.put("取件姓名", l.getFetchName());
            infos.put("取件地址", l.getFetchAddr());
            infos.put("本单费用", String.format("%.2f", l.getTotalFee() / 100.0));
            infos.put("用户备注", l.getRemark());
            infos.put("快递单号", l.getExpNo());
            infos.put("寄件补收", expChargeService.sumExtraFee(l.getId()));
            obj.put("infos", infos);
            array.add(obj);
        }

        return array;
    }

    //    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        Asserts.check(readOnly(), "read only mananger!");
        try {
            sendService.updateAndSave(getSessionPersistent(Manager.class), map);
        } catch (Exception e) {
            logger.error("update expSend order failed!", e);
            return fail(e.getMessage());
        }

        return success();
    }

    @GetMapping("accept")
    public Object accept(@RequestParam Long unifiedId) {
        Asserts.check(readOnly(), "read only mananger!");
        ExpSendOrder expSendOrder = sendService.findById(unifiedId);
        if (expSendOrder == null || !(OrderStatusEnum.PAIED.getCode() == expSendOrder.getStatus())) {
            return fail("status not right!");
        }
        expSendOrder.setStatus(OrderStatusEnum.PROCESSING.getCode());
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(expSendOrder.getUnifiedId());
        unifiedOrder.setStatus(OrderStatusEnum.PROCESSING.getCode());
        unifiedOrderService.update(unifiedOrder);

        return success();
    }

    @GetMapping("complete")
    public Object complete(@RequestParam Long unifiedId, @RequestParam String expNo) {
        Asserts.check(readOnly(), "read only mananger!");
        if (hasEmptyString(expNo)) {
            return fail("快递单号未录入!");
        }
        ExpSendOrder expSendOrder = sendService.findById(unifiedId);
        if (expSendOrder == null || !(OrderStatusEnum.PROCESSING.getCode() == expSendOrder.getStatus())) {
            return fail("status not right!");
        }
        expSendOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        expSendOrder.setExpNo(expNo);
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(expSendOrder.getUnifiedId());
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
            sendService.cancel(unifiedOrder);
            return success();
        }
        return fail("退款失败");
    }
}
