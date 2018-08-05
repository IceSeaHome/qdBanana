package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.BaseService;

import java.util.List;

@Service
public class ExpTakeService extends BaseService<ExpTakeOrder> implements UnifiedOrderMethods {

    @Override
    public Object moreInfo(UnifiedOrder order) {
        ExpTakeOrder takeOrder = new ExpTakeOrder();
        takeOrder.setUnifiedId(order.getId());
        return queryOne(takeOrder);
    }

    @Override
    public Object cancel(UnifiedOrder order) {
        ExpTakeOrder takeOrder = (ExpTakeOrder) moreInfo(order);
        takeOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(takeOrder);
        return takeOrder;
    }

    public List<ExpTakeOrder> findByStatus(Integer status, Integer page, Integer pageSize) {
        ExpTakeOrder order = new ExpTakeOrder();
        order.setStatus(status);
        return sortQuery(order,"id",true);
    }
}
