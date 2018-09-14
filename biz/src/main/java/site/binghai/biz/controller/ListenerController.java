package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.CommonPayOrderService;
import site.binghai.biz.service.ExpChargeService;
import site.binghai.biz.service.ExpSendService;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
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
    private ExpSendService expSendService;
    @Autowired
    private ExpTakeService expTakeService;
    @Autowired
    private ExpChargeService expChargeService;
    @Autowired
    private CommonPayOrderService commonPayOrderService;


    @RequestMapping("payNotify")
    public Object payNotify(@RequestParam Long totalPay, @RequestParam String orderId, @RequestParam String sign) {
        if (hasEmptyString(totalPay, orderId, sign)) {
            return fail("all parameters is required.");
        }

        if (!MD5.encryption(totalPay + orderId + iceConfig.getWxValidateMD5Key()).equals(sign)) {
            return fail("Illegal signature");
        }

        UnifiedOrder unifiedOrder = unifiedOrderService.findByOrderId(orderId);
        if (unifiedOrder == null || unifiedOrder.getStatus() >= OrderStatusEnum.PAIED.getCode()) {
            return fail("status not right!");
        }

        unifiedOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        unifiedOrderService.update(unifiedOrder);

        payEvent(unifiedOrder);
        return success();
    }

    private void payEvent(UnifiedOrder unifiedOrder) {
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                expSendService.onPaid(unifiedOrder);
                break;
            case EXP_TAKE:
                expTakeService.onPaid(unifiedOrder);
                break;
            case EXP_CHARGE:
                expChargeService.onPaid(unifiedOrder);
                break;
            case COMMON_PAY:
                commonPayOrderService.onPaid(unifiedOrder);
                break;
        }
    }
}
