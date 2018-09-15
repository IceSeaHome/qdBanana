package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.entity.VipChargeOrder;
import site.binghai.biz.entity.VipPkg;
import site.binghai.biz.service.*;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.MD5;

@RestController
@RequestMapping("/")
public class ListenerController extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;
    @Autowired
    private WxUserService wxUserService;


    @RequestMapping("payNotify")
    public Object payNotify(@RequestParam Long totalPay, @RequestParam String orderId, @RequestParam String sign) {
        if (hasEmptyString(totalPay, orderId, sign)) {
            return fail("all parameters is required.");
        }

        if (!MD5.encryption(totalPay + orderId + iceConfig.getWxValidateMD5Key()).equals(sign)) {
            return fail("Illegal signature");
        }

        try {
            payBizServiceFactory.onPayNotify(orderId);
        } catch (Exception e) {
            logger.error("onPayNotify error !totalPay:{},orderId:{},sign:{}", totalPay, orderId, sign, e);
            return fail(e.getMessage());
        }
        return success();
    }


}
