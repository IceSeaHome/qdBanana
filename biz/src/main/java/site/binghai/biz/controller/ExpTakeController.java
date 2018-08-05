package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpBrand;
import site.binghai.biz.entity.ExpTakeOrder;
import site.binghai.biz.service.ExpBrandService;
import site.binghai.biz.service.ExpTakeService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 取件业务逻辑
 */
@RestController
@RequestMapping("/user/expTake/")
public class ExpTakeController extends BaseController {

    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private ExpTakeService expTakeService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("prepare")
    public Object prepare() {
        Map map = new HashMap();

        return success(map, null);
    }

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        ExpTakeOrder order = expTakeService.newInstance(map);
        Long expId = order.getExpId();
        ExpBrand expBrand = expBrandService.findById(expId);
        order.setExpName(expBrand.getExpName());

        WxUser user = getSessionPersistent(WxUser.class);

        // 补充业务逻辑
        if (!expBrand.getEnableTake()) {
            return fail(expBrand.getExpName() + "不支持取件业务!");
        }

        order.setStatus(OrderStatusEnum.CREATED.getCode());
        order.setPaid(Boolean.FALSE);
        order.setTotalFee(expBrand.getTakeServiceFee());
        order.setUserId(user.getId());

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(
            PayBizEnum.EXP_TAKE,
            user,
            expBrand.getExpName() + "代取",
            order.getTotalFee().intValue());

        order.setUnifiedId(unifiedOrder.getId());

        order = expTakeService.save(order);
        return success(order, null);
    }
}
