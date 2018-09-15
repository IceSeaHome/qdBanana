package site.binghai.lib.def;


import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.PayBizEnum;

import java.util.Map;

/**
 * Created by IceSea on 2018/4/17.
 * GitHub: https://github.com/IceSeaOnly
 * 统一订单的具体子订单实体需实现该接口
 */
public interface UnifiedOrderMethods<T> {
    T moreInfo(UnifiedOrder order);

    T cancel(UnifiedOrder order);

    /**
     * 转换成用户可读的k，v结构
     */
    Map readMap(UnifiedOrder order);

    void onPaid(UnifiedOrder order);

    String readSimpleInfo(UnifiedOrder order);

    Class<T> getTypeArguement();

    PayBizEnum getBizType();

    T newInstance(Map map);

    T save(T t);
}
