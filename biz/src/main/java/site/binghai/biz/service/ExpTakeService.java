package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.biz.service.dao.ExpSendDao;
import site.binghai.biz.service.dao.ExpTakeDao;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.BaseService;

import java.util.List;

@Service
public class ExpTakeService extends BaseService<ExpTakeOrder> implements UnifiedOrderMethods<ExpTakeOrder> {

    @Autowired
    private ExpTakeDao dao;

    @Override
    public ExpTakeDao getDao() {
        return dao;
    }

    @Override
    public ExpTakeOrder moreInfo(UnifiedOrder order) {
        ExpTakeOrder takeOrder = new ExpTakeOrder();
        takeOrder.setUnifiedId(order.getId());
        return queryOne(takeOrder);
    }

    @Override
    public ExpTakeOrder cancel(UnifiedOrder order) {
        ExpTakeOrder takeOrder = moreInfo(order);
        takeOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(takeOrder);
        return takeOrder;
    }

    public List<ExpTakeOrder> findByStatusIdDesc(Integer status, Integer page, Integer pageSize) {
        return dao.findAllByStatusOrderByIdDesc(status, new PageRequest(page, pageSize));
    }

    public List<ExpTakeOrder> findTimeBetween(Long timeStart, Long timeEnd) {
        return dao.findByCreatedBetween(timeStart, timeEnd);
    }
}
