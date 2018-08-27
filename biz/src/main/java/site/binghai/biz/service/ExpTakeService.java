package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.biz.service.dao.ExpTakeDao;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.utils.StringUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map readMap(UnifiedOrder order) {
        ExpTakeOrder entity = moreInfo(order);

        LinkedHashMap ret = new LinkedHashMap();
        ret.put("订单总额", entity.getTotalFee() / 100.0);
        ret.put("快递品牌", entity.getExpTakeName());
        ret.put("取件短信", entity.getSmsText());
        ret.put("取件姓名", entity.getExpTakeName());
        ret.put("取件电话", entity.getExpTakePhone());
        ret.put("配送地址", entity.getSendAddr());
        ret.put("配送电话", entity.getSendPhone());
        ret.put("订单备注", entity.getRemark());
        return ret;
    }

    @Override
    public void onPaid(UnifiedOrder order) {
        ExpTakeOrder expTakeOrder = moreInfo(order);
        expTakeOrder.setPaid(true);
        expTakeOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        update(expTakeOrder);
    }

    @Override
    public String readSimpleInfo(UnifiedOrder order) {
        ExpTakeOrder takeOrder = moreInfo(order);
        return takeOrder.getExpTakeName() + "/" + StringUtil.shorten(takeOrder.getSmsText(), 10);
    }

    public List<ExpTakeOrder> findByStatusIdDesc(Integer status, Integer page, Integer pageSize) {
        return dao.findAllByStatusOrderByIdDesc(status, new PageRequest(page, pageSize));
    }

    public List<ExpTakeOrder> findTimeBetween(Long timeStart, Long timeEnd) {
        return dao.findByCreatedBetween(timeStart, timeEnd);
    }
}
