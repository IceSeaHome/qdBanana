package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpBrand;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.entity.VipChargeOrder;
import site.binghai.biz.entity.VipPkg;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.BaseBean;
import site.binghai.lib.utils.CompareUtils;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayBizServiceFactory extends BaseBean {
    private static Map<PayBizEnum, UnifiedOrderMethods> serviceMap;

    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    public UnifiedOrderMethods get(PayBizEnum payBizEnum) {
        return serviceMap.get(payBizEnum);
    }

    public UnifiedOrderMethods get(int code) {
        return serviceMap.get(PayBizEnum.valueOf(code));
    }

    public String buildPayUrl(UnifiedOrder unifiedOrder) {
        return "/user/unified/multiPay?unifiedId=" + unifiedOrder.getId();
    }

    public String buildWxPayUrl(UnifiedOrder unifiedOrder) {
        String url = iceConfig.getWxPayUrl()
                + "?title=" + unifiedOrder.getTitle()
                + "&totalFee=" + unifiedOrder.getShouldPay()
                + "&orderId=" + unifiedOrder.getOrderId();
        if (unifiedOrder.getAppCode().equals(PayBizEnum.EXP_SEND.getCode())) {
            ExpSendService expSendService = (ExpSendService) get(unifiedOrder.getAppCode());
            ExpSendOrder expSendOrder = expSendService.moreInfo(unifiedOrder);
            ExpBrand expBrand = expBrandService.findById(expSendOrder.getExpId());
            return url + "&callBack=" + expBrand.getServiceUrl();
        }
        return url + "&callBack=" + iceConfig.getAppRoot() + "/user/unified/detail?unifiedId=" + unifiedOrder.getId();
    }

    @Autowired
    public void setAll(List<UnifiedOrderMethods> bizs) {
        serviceMap = new HashMap<>();
        if (isEmptyList(bizs)) return;

        for (UnifiedOrderMethods biz : bizs) {
            serviceMap.put(biz.getBizType(), biz);
            logger.info("PayBizServiceFactory loaded service {} for {},",
                    biz.getClass().getSimpleName(), biz.getBizType().getName());
        }
    }


    @Transactional
    public void onPayNotify(String orderId) throws Exception {
        UnifiedOrder unifiedOrder = unifiedOrderService.findByOrderId(orderId);
        if (unifiedOrder == null || unifiedOrder.getStatus() >= OrderStatusEnum.PAIED.getCode()) {
            throw new Exception("status not right!");
        }

        if(CompareUtils.inAny(PayBizEnum.valueOf(unifiedOrder.getAppCode()),PayBizEnum.COMMON_PAY,PayBizEnum.VIP_CHARGE)){
            unifiedOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        }else {
            unifiedOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        }

        unifiedOrderService.update(unifiedOrder);

        payEvent(unifiedOrder);
    }

    private void payEvent(UnifiedOrder unifiedOrder) {
        get(unifiedOrder.getAppCode()).onPaid(unifiedOrder);
        afterPaid(unifiedOrder);
    }

    private void afterPaid(UnifiedOrder unifiedOrder) {
        PayBizEnum payBiz = PayBizEnum.valueOf(unifiedOrder.getAppCode());

        if (payBiz.equals(PayBizEnum.VIP_CHARGE)) {
            VipChargeOrderService service = (VipChargeOrderService) get(payBiz);
            WxUser wxUser = wxUserService.findById(unifiedOrder.getUserId());
            VipChargeOrder vipChargeOrder = service.moreInfo(unifiedOrder);
            VipPkg pkg = JSONObject.parseObject(vipChargeOrder.getPkgInfo(), VipPkg.class);
            if (wxUser.getWallet() == null) {
                wxUser.setWallet(pkg.getRecharge());
            } else {
                wxUser.setWallet(wxUser.getWallet() + pkg.getRecharge());
            }
            wxUserService.update(wxUser);
        }

    }
}
