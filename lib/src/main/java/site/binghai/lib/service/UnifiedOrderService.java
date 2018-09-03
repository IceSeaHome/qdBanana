package site.binghai.lib.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.dao.UnifiedOrderDao;
import site.binghai.lib.utils.CompareUtils;
import site.binghai.lib.utils.HttpUtils;
import site.binghai.lib.utils.MD5;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by IceSea on 2018/4/5.
 * GitHub: https://github.com/IceSeaOnly
 */
@Service
public class UnifiedOrderService extends BaseService<UnifiedOrder> {
    @Autowired
    private UnifiedOrderDao dao;
    @Autowired
    private IceConfig iceConfig;

    @Override
    protected JpaRepository<UnifiedOrder, Long> getDao() {
        return dao;
    }

    public List<UnifiedOrder> findByUserIdOrderByIdDesc(Long userId, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;
        return dao.findAllByUserIdOrderByIdDesc(userId, new PageRequest(page, pageSize));
    }

    public List<UnifiedOrder> findByAppCode(PayBizEnum pbe, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;
        return dao.findAllByAppCodeOrderByCreatedDesc(pbe.getCode(), new PageRequest(page, pageSize));
    }

    public List<UnifiedOrder> findByAppCode(PayBizEnum pbe, Long categoryId, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;
        return dao.findAllByAppCodeOrderByCreatedDesc(pbe.getCode(), new PageRequest(page, pageSize));
    }

    public List<UnifiedOrder> findByAppCodeAndUserId(PayBizEnum pb,Long userId){
        UnifiedOrder example = new UnifiedOrder();
        example.setUserId(userId);
        example.setAppCode(pb.getCode());
        return query(example);
    }

    public Long countByCode(PayBizEnum pb) {
        return dao.countByAppCode(pb.getCode());
    }

    public Long countByAppCodeAndStatus(PayBizEnum pb, Integer status) {
        return dao.countByAppCodeAndStatus(pb.getCode(), status);
    }

    public List<UnifiedOrder> list(PayBizEnum payBiz, OrderStatusEnum status, Integer page, Integer pageSize) {
        if (page == null || page < 0) page = 0;
        if (pageSize == null || pageSize < 0) pageSize = 100;

        return dao.findAllByAppCodeAndStatusOrderByCreatedDesc(payBiz.getCode(), status.getCode(), new PageRequest(page, pageSize));
    }

    public UnifiedOrder newOrder(PayBizEnum biz, WxUser user, String title, int payMuch) {
        UnifiedOrder order = new UnifiedOrder();
        order.setAppCode(biz.getCode());
        order.setCouponId(null);
        order.setOpenId(user.getOpenId());
        order.setUserId(user.getId());
        order.setUserName(user.getUserName());
        order.setUserPhone(user.getPhone());
        order.setTitle(title);
        order.setShouldPay(payMuch);
        order.setOriginalPrice(payMuch);
        return save(order);
    }


    public UnifiedOrder findByOrderId(String orderKey) {
        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setStatus(null);
        unifiedOrder.setOrderId(orderKey);
        return queryOne(unifiedOrder);
    }

    /**
     * @Params search : 用户姓名或手机号中的某几位
     */
    public List<UnifiedOrder> findBySearchWords(String search) {
        List<UnifiedOrder> res = emptyList();

        UnifiedOrder unifiedOrder = new UnifiedOrder();
        unifiedOrder.setOrderId(null);
        unifiedOrder.setStatus(null);
        unifiedOrder.setUserName(search);

        res.addAll(query(unifiedOrder));
        unifiedOrder.setUserName(null);
        unifiedOrder.setUserPhone(search);

        res.addAll(query(unifiedOrder));
        return res;
    }

    @Transactional
    public boolean cancel(Long unifiedId) {
        UnifiedOrder order = findById(unifiedId);
        if (order == null) return false;
        if (order.getStatus().equals(OrderStatusEnum.CANCELED.getCode())) return false;

        boolean execute = false;
        if (CompareUtils.inAny(OrderStatusEnum.valueOf(order.getStatus()), OrderStatusEnum.PROCESSING,
                OrderStatusEnum.COMPLETE, OrderStatusEnum.PAIED)) {

            String tradeNo = order.getOrderId();
            String totalFee = order.getShouldPay().toString();
            String sign = MD5.encryption(tradeNo + iceConfig.getWxValidateMD5Key() + totalFee);

            String urlParams = "out_trade_no=" + tradeNo + "&refund_fee=" + totalFee + "&validate=" + sign;
            String ret = HttpUtils.sendGet(iceConfig.getWxRefundUrl(), urlParams);

            logger.warn("Refund url:{}", iceConfig.getWxRefundUrl() + "?" + urlParams);
            logger.warn("Refund Result:{}", ret);

            if (ret.contains("SUCCESS")) {
                execute = true;
            }
        }

        if (execute) {
            order.setStatus(OrderStatusEnum.CANCELED.getCode());
            update(order);
        }
        return execute;
    }

    public List<UnifiedOrder> findByAppCodeAndCreateBetween(PayBizEnum biz, Long timesmorning, Long timesnight) {
        return dao.findAllByAppCodeAndCreatedBetween(biz.getCode(), timesmorning, timesnight);
    }

    public long countByStatusIn(OrderStatusEnum... statusEnums) {
        long sum = 0l;
        for (OrderStatusEnum status : statusEnums) {
            sum += dao.countByStatus(status.getCode());
        }
        return sum;
    }
}
