package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.ExpSendService;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;

@RestController
@RequestMapping("/user/unified/")
public class UnifiedOrderController extends BaseController {

    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private ExpTakeService takeService;
    @Autowired
    private ExpSendService expSendService;

    @GetMapping("cancel")
    public Object cancel(@RequestParam Long unifiedId) {
        UnifiedOrder unifiedOrder = unifiedOrderService.findById(unifiedId);
        WxUser wxUser = getSessionPersistent(WxUser.class);

        if (unifiedId == null || !unifiedOrder.getUserId().equals(wxUser.getId())) {
            return fail("身份认证不通过");
        }

        switch (OrderStatusEnum.valueOf(unifiedOrder.getStatus())) {
            case CREATED:
            case PAYING:
            case PAIED:
                return cancelUnifiedOrder(unifiedOrder);
            case PROCESSING:
                return cancelProcessingOrder(unifiedOrder);
            case REFUNDING:
            case CANCELED:
                return fail("该订单已取消或正在取消中，无法再次取消!");
            case COMPLETE:
                return fail("订单已完成，无法取消，如有疑问请咨询客服!");
            default:
                return fail("订单状态不正确，无法取消");
        }
    }

    /**
     * 取消处理中的订单，暂时直接拒绝
     */
    private Object cancelProcessingOrder(UnifiedOrder unifiedOrder) {
        return fail("订单处理中，无法取消，请联系客服");
    }

    /**
     * 取消未处理的订单: 退款+取消
     */
    private Object cancelUnifiedOrder(UnifiedOrder unifiedOrder) {
        if (OrderStatusEnum.valueOf(unifiedOrder.getStatus()) == OrderStatusEnum.PAIED) {
            if (refund(unifiedOrder)) {
                return cancelBizOrder(unifiedOrder);
            }
            return fail("取消失败-REFUND-FAIL");
        } else {
            unifiedOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
            unifiedOrderService.update(unifiedOrder);
            return cancelBizOrder(unifiedOrder);
        }
    }

    private Object cancelBizOrder(UnifiedOrder unifiedOrder) {
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                return expSendService.cancel(unifiedOrder);
            case EXP_TAKE:
                return expSendService.cancel(unifiedOrder);
            default:
                return fail("取消失败-BIZ-NOT-SUPPORT");
        }
    }

    /**
     * 退款
     */
    private boolean refund(UnifiedOrder unifiedOrder) {
        return false;
    }
}
