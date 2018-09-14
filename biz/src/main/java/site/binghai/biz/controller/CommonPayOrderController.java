package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.CommonPayOrder;
import site.binghai.biz.entity.ExpChargeOrder;
import site.binghai.biz.service.CommonPayOrderService;
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
@RequestMapping("/user/commonPay/")
public class CommonPayOrderController extends BaseController {
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private CommonPayOrderService commonPayOrderService;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        return "paySendFee";
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

        String payName = getString(map,"payName");
        CommonPayOrder charge = commonPayOrderService.newInstance(map);

        WxUser user = getSessionPersistent(WxUser.class);

        charge.setStatus(OrderStatusEnum.CREATED.getCode());
        charge.setPaid(Boolean.FALSE);
        charge.setUserId(user.getId());
        charge.setPayName(payName);

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(
                PayBizEnum.COMMON_PAY,
                user,
                payName,
                charge.getFee().intValue());

        charge.setUnifiedId(unifiedOrder.getId());

        charge = commonPayOrderService.save(charge);
        return success(charge, "/user/unified/detail?unifiedId=" + unifiedOrder.getId());
    }
}
