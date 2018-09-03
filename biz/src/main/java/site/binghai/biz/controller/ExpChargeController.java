package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpChargeOrder;
import site.binghai.biz.service.ExpChargeService;
import site.binghai.biz.service.ExpSendService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.CompareUtils;
import site.binghai.lib.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/expCharge/")
public class ExpChargeController extends BaseController {
    @Autowired
    private ExpChargeService chargeService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private ExpSendService expSendService;
    @Autowired
    private UnifiedOrderController unifiedOrderController;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        WxUser user = getSessionPersistent(WxUser.class);
        List<UnifiedOrder> data =
                unifiedOrderService.findByAppCodeAndUserId(PayBizEnum.EXP_SEND, user.getId());

        if (!isEmptyList(data)) {
            data.stream()
                    .filter(v -> CompareUtils.inAny(OrderStatusEnum.valueOf(v.getStatus()),
                            OrderStatusEnum.PAIED, OrderStatusEnum.COMPLETE, OrderStatusEnum.PROCESSING))
                    .sorted((a, b) -> b.getId() > a.getId() ? 1 : -1)
                    .collect(Collectors.toList());
            data.forEach(v -> {
                JSONObject extra = newJSONObject();
                extra.put("sinfo", expSendService.readSimpleInfo(v));
                extra.put("payUrl", unifiedOrderController.buildPayUrl(v));
                v.setExtra(extra);
                v.setOrderId(StringUtil.shorten(v.getOrderId(), 12) + "...");
            });
        }

        map.put("orderParts", data);
        return "chargePreview";
    }

    @PostMapping("create")
    @ResponseBody
    public Object create(@RequestBody Map map) {
        int fee = 0;
        try {
            double dfee = getDouble(map, "fee");
            fee = new Double(dfee * 100).intValue();
            if (fee <= 0) throw new Exception();
            map.put("fee", fee);
        } catch (Exception e) {
            return fail("费用输入有误!");
        }

        ExpChargeOrder charge = chargeService.newInstance(map);

        WxUser user = getSessionPersistent(WxUser.class);

        charge.setStatus(OrderStatusEnum.CREATED.getCode());
        charge.setPaid(Boolean.FALSE);
        charge.setUserId(user.getId());

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(
                PayBizEnum.EXP_CHARGE,
                user,
                "寄件费" + charge.getFee(),
                charge.getFee().intValue());

        charge.setUnifiedId(unifiedOrder.getId());

        charge = chargeService.save(charge);
        return success(charge, "/user/unified/detail?unifiedId=" + unifiedOrder.getId());
    }
}
