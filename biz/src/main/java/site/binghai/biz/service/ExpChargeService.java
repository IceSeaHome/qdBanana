package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpChargeOrder;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.List;
import java.util.Map;

@Service
public class ExpChargeService extends BaseService<ExpChargeOrder> implements UnifiedOrderMethods<ExpChargeOrder> {
    @Autowired
    private ExpSendService expSendService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @Override
    public ExpChargeOrder moreInfo(UnifiedOrder order) {
        ExpChargeOrder exp = new ExpChargeOrder();
        exp.setUnifiedId(order.getId());
        return queryOne(exp);
    }

    @Override
    public ExpChargeOrder cancel(UnifiedOrder order) {
        ExpChargeOrder expChargeOrder = moreInfo(order);
        expChargeOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(expChargeOrder);
        return expChargeOrder;
    }

    @Override
    public Map readMap(UnifiedOrder order) {
        ExpChargeOrder expChargeOrder = moreInfo(order);
        ExpSendOrder expSendOrder = expSendService.findById(expChargeOrder.getExpId());
        return expSendService.readMap(unifiedOrderService.findById(expSendOrder.getUnifiedId()));
    }

    public void onPaid(UnifiedOrder order) {
        ExpChargeOrder expChargeOrder = moreInfo(order);
        expChargeOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        expChargeOrder.setPaid(true);
        update(expChargeOrder);
    }

    @Override
    public String readSimpleInfo(UnifiedOrder order) {
        ExpChargeOrder expChargeOrder = moreInfo(order);
        return "寄件费用附加 " + expChargeOrder.getFee() / 100.0 + "元";
    }

    public String sumExtraFee(Long expSendOrderId) {
        List<ExpChargeOrder> orders = findPaidExpSendOrder(expSendOrderId);
        if (isEmptyList(orders)) return "0元";
        int sum = orders.stream().map(v -> v.getFee()).reduce((a, b) -> a + b).orElse(0);
        return sum / 100.0 + "元";
    }

    private List<ExpChargeOrder> findPaidExpSendOrder(Long expSendOrderId) {
        ExpChargeOrder exp = new ExpChargeOrder();
        exp.setPaid(true);
        exp.setExpId(expSendOrderId);
        return query(exp);
    }
}
