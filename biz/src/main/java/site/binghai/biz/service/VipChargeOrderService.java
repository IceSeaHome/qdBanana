package site.binghai.biz.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.VipChargeOrder;
import site.binghai.biz.entity.VipPkg;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VipChargeOrderService extends BaseService<VipChargeOrder> implements UnifiedOrderMethods<VipChargeOrder> {
    @Override
    public VipChargeOrder moreInfo(UnifiedOrder order) {
        VipChargeOrder vipChargeOrder = new VipChargeOrder();
        vipChargeOrder.setUnifiedId(order.getId());
        return queryOne(vipChargeOrder);
    }

    @Override
    public VipChargeOrder cancel(UnifiedOrder order) {
        VipChargeOrder vipChargeOrder = moreInfo(order);
        vipChargeOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        return update(vipChargeOrder);
    }

    @Override
    public Map readMap(UnifiedOrder order) {
        Map map = new LinkedHashMap();
        VipChargeOrder vipChargeOrder = moreInfo(order);
        VipPkg pkg = JSONObject.parseObject(vipChargeOrder.getPkgInfo(), VipPkg.class);
        map.put("充值套餐", pkg.getName());
        map.put("套餐价格", pkg.getPrice() / 100.0 + "元");
        map.put("充值金额", pkg.getRecharge() / 100.0 + "元");
        return map;
    }

    @Override
    public void onPaid(UnifiedOrder order) {
        VipChargeOrder vipChargeOrder = moreInfo(order);
        vipChargeOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        update(vipChargeOrder);
    }

    @Override
    public String readSimpleInfo(UnifiedOrder order) {
        VipChargeOrder vipChargeOrder = moreInfo(order);
        VipPkg pkg = JSONObject.parseObject(vipChargeOrder.getPkgInfo(), VipPkg.class);
        return pkg.getName()+"-VIP充值";
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.VIP_CHARGE;
    }
}
