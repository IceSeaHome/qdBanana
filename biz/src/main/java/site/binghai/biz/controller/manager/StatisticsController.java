package site.binghai.biz.controller.manager;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.TimeTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/statistics/")
public class StatisticsController extends BaseController {
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("bizShow")
    public Object bizShow() {
        JSONObject data = new JSONObject();
        Long[] today = TimeTools.today();
        Long[] yesterday = TimeTools.yesterday();

        data.putAll(getBizIndexOfTimeBetween(PayBizEnum.EXP_TAKE, today));
        data.putAll(getBizIndexOfTimeBetween(PayBizEnum.EXP_TAKE, yesterday));
        data.putAll(getBizIndexOfTimeBetween(PayBizEnum.EXP_SEND, today));
        data.putAll(getBizIndexOfTimeBetween(PayBizEnum.EXP_SEND, yesterday));

        //总客户数，总支付数
        long wxUserSum = wxUserService.count();
        long paidSum = unifiedOrderService.countByStatusIn(OrderStatusEnum.PAIED, OrderStatusEnum.PROCESSING, OrderStatusEnum.COMPLETE);
        //新增客户数，今日昨日
        long todayNewWxUser = wxUserService.countByCreatedBetween(TimeTools.today());
        long yesterdayNewWxUser = wxUserService.countByCreatedBetween(TimeTools.yesterday());

        data.put("WX_USER_SUM", wxUserSum);
        data.put("WX_PAID_ORDER_SUM", paidSum);
        data.put("TODAY_NEW_USER", todayNewWxUser);
        data.put("YESTERDAY_NEW_USER", yesterdayNewWxUser);

        return success(data, null);
    }

    private Map getBizIndexOfTimeBetween(PayBizEnum biz, Long[] between) {
        Map<String, Object> res = new HashMap<>();
        List<UnifiedOrder> orders = listBizOrders(biz, between);
        Integer[] index = getIncomesAndOrderSum(orders);
        res.put(biz.name() + "_INCOME", index[0]);
        res.put(biz.name() + "_ORDER_SIZE", index[1]);
        return res;
    }

    private List<UnifiedOrder> listBizOrders(PayBizEnum biz, Long[] tsBetween) {
        return unifiedOrderService.findByAppCodeAndCreateBetween(biz, tsBetween[0], tsBetween[1]);
    }

    private Integer[] getIncomesAndOrderSum(List<UnifiedOrder> orders) {
        Integer incomes = 0;
        for (UnifiedOrder order : orders) {
            incomes += order.getShouldPay();
        }
        return new Integer[]{incomes, orders.size()};
    }
}
