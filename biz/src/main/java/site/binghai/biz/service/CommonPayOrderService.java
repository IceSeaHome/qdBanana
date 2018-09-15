package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.CommonPayOrder;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommonPayOrderService extends BaseService<CommonPayOrder> implements UnifiedOrderMethods<CommonPayOrder> {
    @Override
    public CommonPayOrder moreInfo(UnifiedOrder order) {
        CommonPayOrder commonPayOrder = new CommonPayOrder();
        commonPayOrder.setUnifiedId(order.getId());
        return queryOne(commonPayOrder);
    }

    @Override
    public CommonPayOrder cancel(UnifiedOrder order) {
        CommonPayOrder commonPayOrder = moreInfo(order);
        commonPayOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        return update(commonPayOrder);
    }

    @Override
    public Map readMap(UnifiedOrder order) {
        CommonPayOrder commonPayOrder = moreInfo(order);
        Map data = new HashMap();
        data.put("业务名称", commonPayOrder.getPayName());
        data.put("扩展信息", commonPayOrder.getRemark());
        return data;
    }

    @Override
    public void onPaid(UnifiedOrder order) {
        CommonPayOrder commonPayOrder = moreInfo(order);
        commonPayOrder.setPaid(true);
        commonPayOrder.setStatus(OrderStatusEnum.COMPLETE.getCode());
        update(commonPayOrder);
    }

    @Override
    public String readSimpleInfo(UnifiedOrder order) {
        CommonPayOrder commonPayOrder = moreInfo(order);
        return commonPayOrder.getPayName() + "/" + commonPayOrder.getRemark();
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.COMMON_PAY;
    }
}
