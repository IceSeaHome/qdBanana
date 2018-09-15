package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.service.dao.ExpSendDao;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.BaseService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpSendService extends BaseService<ExpSendOrder> implements UnifiedOrderMethods<ExpSendOrder> {
    @Autowired
    private ExpSendDao dao;
    @Autowired
    private ExpChargeService expChargeService;

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
        ExpSendOrder expSendOrder = moreInfo(order);
        Map data = new LinkedHashMap();
        data.put("下单时间", expSendOrder.getCreatedTime());
        if(!hasEmptyString(expSendOrder.getExpNo())){
            data.put("快递单号",expSendOrder.getExpNo());
        }
        data.put("快递名称",expSendOrder.getExpName());
        data.put("联系姓名",expSendOrder.getFetchName());
        data.put("联系手机",expSendOrder.getFetchPhone());
        data.put("取件地址",expSendOrder.getFetchAddr());
        data.put("订单备注",expSendOrder.getRemark());
        data.put("订单总额",expSendOrder.getTotalFee()/100.0);
        data.put("寄件补收",expChargeService.sumExtraFee(expSendOrder.getId()));
        return data;
    }

    @Override
    public void onPaid(UnifiedOrder order) {
        ExpSendOrder expSendOrder = moreInfo(order);
        expSendOrder.setStatus(OrderStatusEnum.PAIED.getCode());
        expSendOrder.setPaid(true);
        update(expSendOrder);
    }

    @Override
    public String readSimpleInfo(UnifiedOrder order) {
        ExpSendOrder expSendOrder = moreInfo(order);
        return expSendOrder.getExpName();
    }

    @Override
    public PayBizEnum getBizType() {
        return PayBizEnum.EXP_SEND;
    }


    public List<ExpSendOrder> findTimeBetween(Long timeStart, Long timeEnd) {
        return dao.findByCreatedBetween(timeStart, timeEnd);
    }

    public List<ExpSendOrder> findByStatusIdDesc(Integer status, Integer page, Integer pageSize) {
        return dao.findAllByStatusOrderByIdDesc(status, new PageRequest(page, pageSize));
    }
}
