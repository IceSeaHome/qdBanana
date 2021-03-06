package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.service.*;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.StringUtil;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user/unified/")
public class UnifiedOrderController extends BaseController {

    @Autowired
    private UnifiedOrderService unifiedOrderService;
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private PayBizServiceFactory payBizServiceFactory;

    @GetMapping("detail")
    public String detail(@RequestParam Long unifiedId, ModelMap map) {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order == null || !order.getUserId().equals(wxUser.getId())) {
            return "authenticationFail";
        }

        order.setExtra(readMap(order));
        map.put("order", order);
        map.put("payUrl", payBizServiceFactory.buildPayUrl(order));
        return "detail";
    }

    @GetMapping("walletPay")
    public String walletPay(@RequestParam Long unifiedId, String callBack, ModelMap map) {
        WxUser wxUser = updateSessionUser();
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (wxUser.getWallet() == null || wxUser.getWallet() < order.getShouldPay()) {
            return "redirect:detail?unifiedId=" + unifiedId;
        }

        wxUser.setWallet(wxUser.getWallet() - order.getShouldPay());
        wxUserService.update(wxUser);

        try {
            payBizServiceFactory.onPayNotify(order.getOrderId());
        } catch (Exception e) {
            logger.error("wallet pay failed,{}", order, e);
        }

        return "redirect:" + (callBack == null ? "detail?unifiedId=" + unifiedId : callBack);
    }

    @GetMapping("multiPay")
    public String multiPay(@RequestParam Long unifiedId, ModelMap map) {
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        WxUser wxUser = updateSessionUser();
        boolean enableWalletPay = true;
        if (wxUser.getWallet() == null || wxUser.getWallet() < order.getShouldPay()) {
            enableWalletPay = false;
        }

        if (order.getAppCode().equals(PayBizEnum.VIP_CHARGE.getCode())) {
            enableWalletPay = false;
        }

        map.put("enableWalletPay", enableWalletPay);
        map.put("wxPayUrl", payBizServiceFactory.buildWxPayUrl(order));
        map.put("walletPayUrl",
            "/user/unified/walletPay?unifiedId=" + unifiedId + payBizServiceFactory.buildCallbackUrl(order));
        return "multiPay";
    }

    private WxUser updateSessionUser() {
        WxUser user = getSessionPersistent(WxUser.class);
        user = wxUserService.findById(user.getId());
        persistent(user);
        return user;
    }

    private Map readMap(UnifiedOrder unifiedOrder) {
        return payBizServiceFactory
            .get(unifiedOrder.getAppCode())
            .readMap(unifiedOrder);
    }

    @GetMapping("list")
    public String list(ModelMap map) {
        List orderParts = emptyList();
        List all = emptyList();
        List completed = emptyList();
        List created = emptyList();
        WxUser user = getSessionPersistent(WxUser.class);
        List<UnifiedOrder> data = unifiedOrderService.findByUserIdOrderByIdDesc(user.getId(), 0, 1000);
        data.forEach(v -> {
            JSONObject extra = newJSONObject();
            extra.put("sinfo", readSimpleInfo(v));
            extra.put("payUrl", payBizServiceFactory.buildPayUrl(v));
            v.setExtra(extra);
            v.setOrderId(StringUtil.shorten(v.getOrderId(), 12) + "...");
            switch (OrderStatusEnum.valueOf(v.getStatus())) {
                case COMPLETE:
                    completed.add(v);
                    break;
                case CREATED:
                    created.add(v);
                    break;
            }
            all.add(v);
        });
        orderParts.add(all);
        orderParts.add(completed);
        orderParts.add(created);

        map.put("orderParts", orderParts);

        return "orders";
    }

    private Object readSimpleInfo(UnifiedOrder unifiedOrder) {
        return payBizServiceFactory
            .get(unifiedOrder.getAppCode())
            .readSimpleInfo(unifiedOrder);
    }

    @GetMapping("pay")
    @ResponseBody
    public Object pay(@RequestParam Long unifiedId) {
        return "redirect:" + iceConfig.getWxPayUrl() + "?unifiedId=" + unifiedId;
    }

    @GetMapping("cancel")
    @ResponseBody
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
        //todo 取消成功通知
        Object data = null;
        UnifiedOrderMethods service = payBizServiceFactory
            .get(unifiedOrder.getAppCode());

        if (service != null) {
            data = service.cancel(unifiedOrder);
            return success(data, "取消成功");
        } else {
            return fail("取消失败-BIZ-NOT-SUPPORT");
        }
    }

    /**
     * 退款
     */
    private boolean refund(UnifiedOrder unifiedOrder) {
        return unifiedOrderService.cancel(unifiedOrder.getId());
    }
}
