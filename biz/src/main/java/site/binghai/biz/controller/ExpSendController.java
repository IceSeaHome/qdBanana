package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.entity.ExpBrand;
import site.binghai.biz.entity.ExpSendOrder;
import site.binghai.biz.service.ExpBrandService;
import site.binghai.biz.service.ExpSendService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.enums.OrderStatusEnum;
import site.binghai.lib.enums.PayBizEnum;
import site.binghai.lib.service.UnifiedOrderService;

import java.util.Map;

@RestController
@RequestMapping("/user/expSend/")
public class ExpSendController extends BaseController {
    @Autowired
    private ExpSendService expSendService;
    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @PostMapping("create")
    public Object create(@RequestBody Map map) {
        ExpSendOrder order = expSendService.newInstance(map);
        Long expId = order.getExpId();
        ExpBrand expBrand = expBrandService.findById(expId);
        order.setExpName(expBrand.getExpName());

        WxUser user = getSessionPersistent(WxUser.class);

        // 补充业务逻辑
        if (!expBrand.getEnableSend()) {
            return fail(expBrand.getExpName() + "不支持寄件业务!");
        }

        order.setStatus(OrderStatusEnum.CREATED.getCode());
        order.setPaid(Boolean.FALSE);
        order.setTotalFee(expBrand.getTakeServiceFee());
        order.setUserId(user.getId());

        UnifiedOrder unifiedOrder = unifiedOrderService.newOrder(
            PayBizEnum.EXP_SEND,
            user,
            expBrand.getExpName() + "代寄",
            order.getTotalFee().intValue());

        order.setUnifiedId(unifiedOrder.getId());

        order = expSendService.save(order);
        return success(order, null);
    }
}
