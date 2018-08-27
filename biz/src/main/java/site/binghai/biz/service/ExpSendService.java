package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.service.dao.ExpSendDao;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.service.BaseService;

import java.util.List;
import java.util.Map;

@Service
public class ExpSendService extends BaseService<ExpSendOrder> implements UnifiedOrderMethods<ExpSendOrder> {
    @Autowired
    private ExpSendDao dao;

    @Override
    public ExpSendDao getDao() {
        return dao;
    }

    @Override
    public ExpSendOrder moreInfo(UnifiedOrder order) {
        ExpSendOrder exp = new ExpSendOrder();
        exp.setUnifiedId(order.getId());
        return queryOne(exp);
    }

    @Override
    public ExpSendOrder cancel(UnifiedOrder order) {
        ExpSendOrder expSendOrder = moreInfo(order);
        expSendOrder.setStatus(OrderStatusEnum.CANCELED.getCode());
        update(expSendOrder);
        return expSendOrder;
    }

    @Override
    public Map readMap(UnifiedOrder order) {
        return null;
    }

    @Override
    public void onPaid(UnifiedOrder order) {
    }


    public List<ExpSendOrder> findTimeBetween(Long timeStart, Long timeEnd) {
        return dao.findByCreatedBetween(timeStart, timeEnd);
    }

    public List<ExpSendOrder> findByStatusIdDesc(Integer status, Integer page, Integer pageSize) {
        return dao.findAllByStatusOrderByIdDesc(status, new PageRequest(page, pageSize));
    }
}
