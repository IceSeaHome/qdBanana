package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpBrand;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.service.ExpBrandService;
import site.binghai.biz.service.ExpChargeService;
import site.binghai.biz.service.ExpSendService;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
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
    private ExpTakeService takeService;
    @Autowired
    private ExpSendService sendService;
    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private ExpChargeService chargeService;
    @Autowired
    private IceConfig iceConfig;

    @GetMapping("detail")
    public String detail(@RequestParam Long unifiedId, ModelMap map) {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        UnifiedOrder order = unifiedOrderService.findById(unifiedId);
        if (order == null || !order.getUserId().equals(wxUser.getId())) {
            return "authenticationFail";
        }

        order.setExtra(readMap(order));
        map.put("order", order);
        map.put("payUrl", buildPayUrl(order));
        return "detail";
    }

    private String buildPayUrl(UnifiedOrder unifiedOrder) {
        String url = iceConfig.getWxPayUrl()
                + "?title=" + unifiedOrder.getTitle()
                + "&totalFee=" + unifiedOrder.getShouldPay()
                + "&orderId=" + unifiedOrder.getOrderId();
        if (unifiedOrder.getAppCode().equals(PayBizEnum.EXP_SEND.getCode())) {
            ExpSendOrder expSendOrder = sendService.moreInfo(unifiedOrder);
            ExpBrand expBrand = expBrandService.findById(expSendOrder.getExpId());
            return url + "&callBack=" + expBrand.getServiceUrl();
        }
        return url + "&callBack=" + iceConfig.getAppRoot() + "/user/unified/detail?unifiedId=" + unifiedOrder.getId();
    }

    private Map readMap(UnifiedOrder unifiedOrder) {
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                return sendService.readMap(unifiedOrder);
            case EXP_TAKE:
                return takeService.readMap(unifiedOrder);
            case EXP_CHARGE:
                return chargeService.readMap(unifiedOrder);
        }
        return null;
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
            extra.put("payUrl", buildPayUrl(v));
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
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                return sendService.readSimpleInfo(unifiedOrder);
            case EXP_TAKE:
                return takeService.readSimpleInfo(unifiedOrder);
            case EXP_CHARGE:
                return chargeService.readSimpleInfo(unifiedOrder);
        }
        return null;
    }

    @GetMapping("pay")
    @ResponseBody
    public Object pay(@RequestParam Long unifiedId) {
        return "redirect:" + iceConfig.getWxPayUrl() + "?unifiedId=" + unifiedId;
    }

    private Object moreInfo(UnifiedOrder unifiedOrder) {
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                return sendService.moreInfo(unifiedOrder);
            case EXP_TAKE:
                return takeService.moreInfo(unifiedOrder);
            case EXP_CHARGE:
                return chargeService.moreInfo(unifiedOrder);
        }
        return null;
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
        Object data = null;
        //todo 取消成功通知
        switch (PayBizEnum.valueOf(unifiedOrder.getAppCode())) {
            case EXP_SEND:
                data = sendService.cancel(unifiedOrder);
                break;
            case EXP_TAKE:
                data = takeService.cancel(unifiedOrder);
                break;
            case EXP_CHARGE:
                data = chargeService.cancel(unifiedOrder);
                break;
            default:
                return fail("取消失败-BIZ-NOT-SUPPORT");
        }

        return success(data, "取消成功");
    }

    /**
     * 退款
     */
    private boolean refund(UnifiedOrder unifiedOrder) {
        return unifiedOrderService.cancel(unifiedOrder.getId());
    }
}
