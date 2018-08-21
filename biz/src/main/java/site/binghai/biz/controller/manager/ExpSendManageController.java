package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpSendOrder;
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

@RequestMapping("/manage/expSend/")
public class ExpSendManageController extends BaseController {

    @Autowired
    private ExpSendService sendService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("list")
    private Object list(Long timeStart, Long timeEnd) {
        Long[] today = TimeTools.today();
        if (timeEnd == null || timeStart == null) {
            timeStart = today[0];
            timeEnd = today[1];
        }
        List<ExpSendOrder> ls = sendService.findTimeBetween(timeStart, timeEnd);

        JSONObject data = new JSONObject();
        data.put("all", formatData(ls));
        data.put("paid", formatData(ls.stream().filter(v -> v.getPaid()).collect(Collectors.toList())));
        data.put("other", formatData(ls.stream().filter(v -> !v.getPaid()).collect(Collectors.toList())));
        return success(ls, null);
    }

    private Object formatData(List<ExpSendOrder> ls) {
        JSONArray array = new JSONArray();
        for (ExpSendOrder l : ls) {
            JSONObject obj = new JSONObject();
            obj.put("title", l.getExpName());
            obj.put("status", OrderStatusEnum.valueOf(l.getStatus()).getName());
            JSONObject infos = new JSONObject();

            infos.put("流水序号",l.getId());
            infos.put("收单支付",l.getUnifiedId());
            infos.put("用户序号",l.getUserId());
            infos.put("取件手机",l.getFetchPhone());
            infos.put("取件姓名",l.getFetchName());
            infos.put("取件地址",l.getFetchAddr());
            infos.put("本单费用", String.format("%.2f", l.getTotalFee() / 100.0));
            infos.put("用户备注",l.getRemark());
            infos.put("快递单号",l.getExpNo());

            obj.put("infos", infos);
            array.add(obj);
        }

        return array;
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        try {
            sendService.updateAndSave(getSessionPersistent(Manager.class), map);
        } catch (Exception e) {
            logger.error("update expSend order failed!", e);
            return fail(e.getMessage());
        }

        return success();
    }

    @GetMapping("cancel")
    public Object cancel(@RequestParam Long unifiedId) {
        if (unifiedOrderService.cancel(unifiedId)) {
            UnifiedOrder unifiedOrder = new UnifiedOrder();
            unifiedOrder.setId(unifiedId);
            sendService.cancel(unifiedOrder);
        }
        return success();
    }
}
