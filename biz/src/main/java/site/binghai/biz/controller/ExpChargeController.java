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
import site.binghai.biz.service.PayBizServiceFactory;
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
public class ExpChargeController extends PayBizController<ExpChargeOrder> {
    @Autowired
    private ExpChargeService chargeService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private ExpSendService expSendService;
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        WxUser user = getSessionPersistent(WxUser.class);
        List<UnifiedOrder> data =
                unifiedOrderService.findByAppCodeAndUserId(PayBizEnum.EXP_SEND, user.getId());

        if (!isEmptyList(data)) {
            data = data.stream()
                    .filter(v -> CompareUtils.inAny(OrderStatusEnum.valueOf(v.getStatus()),
                            OrderStatusEnum.PAIED, OrderStatusEnum.COMPLETE, OrderStatusEnum.PROCESSING))
                    .peek(v -> {
                        JSONObject extra = newJSONObject();
                        extra.put("expOrderId", expSendService.moreInfo(v).getId());
                        extra.put("sinfo", expSendService.readSimpleInfo(v));
                        extra.put("payUrl", payBizServiceFactory.buildPayUrl(v));
                        v.setExtra(extra);
                        v.setOrderId(StringUtil.shorten(v.getOrderId(), 12) + "...");
                    })
                    .sorted((a, b) -> b.getId() > a.getId() ? 1 : -1)
                    .collect(Collectors.toList());
        }

        map.put("orderParts", data);
        return "chargePreview";
    }

    @PostMapping("create")
    @ResponseBody
    public Object create(@RequestBody Map map) throws Exception {
        int fee = 0;
        try {
            double dfee = getDouble(map, "fee");
            fee = new Double(dfee * 100).intValue();
            if (fee <= 0) throw new Exception();
            map.put("fee", fee);
        } catch (Exception e) {
            return fail("费用输入有误!");
        }

        return create(map, fee);
    }
}
