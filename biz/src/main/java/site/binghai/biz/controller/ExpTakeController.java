package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

import java.util.Map;

/**
 * 取件业务逻辑
 */
@Controller
@RequestMapping("/user/expTake/")
public class ExpTakeController extends BaseController {

    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private ExpTakeService expTakeService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        JSONObject expList = newJSONObject();
        expBrandService.findAll(999)
                .stream()
                .filter(v -> v.getEnableTake())
                .forEach(v -> expList.put(v.getId().toString(), v.getExpName()));

        map.put("wxUser", getSessionPersistent(WxUser.class));
        map.put("expList", expList);
        return "take";
    }

    @PostMapping("create")
    @ResponseBody
    public Object create(@RequestBody Map map) {
        ExpTakeOrder order = expTakeService.newInstance(map);
        Long expId = order.getExpId();
        if (expId == null) return fail("快递必选哦");
        if (hasEmptyString(getString(map, "smsText"))) return fail("取件码必填哦");
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
        return success(order, "/user/unified/detail?unifiedId=" + order.getId());
    }
}
