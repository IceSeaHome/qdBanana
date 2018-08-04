package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.lib.def.UnifiedOrderMethods;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.service.BaseService;

@Service
public class ExpTakeService extends BaseService<ExpTakeOrder> implements UnifiedOrderMethods {

    @Override
    public Object moreInfo(UnifiedOrder order) {
        return null;
    }

    @Override
    public Object cancel(UnifiedOrder order) {
        return null;
    }
}
