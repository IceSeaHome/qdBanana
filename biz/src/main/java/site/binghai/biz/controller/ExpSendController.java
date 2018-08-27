package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/expSend/")
public class ExpSendController extends BaseController {
    @Autowired
    private ExpSendService expSendService;
    @Autowired
    private ExpBrandService expBrandService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("prepare")
    public String prepare(ModelMap map) {
        JSONObject expList = newJSONObject();
        expBrandService.findAll(999)
                .stream()
                .filter(v -> v.getEnableSend())
                .forEach(v -> expList.put(v.getId().toString(), v.getExpName()));

        map.put("wxUser", getSessionPersistent(WxUser.class));
        map.put("expList", expList);
        return "send";
    }

    @PostMapping("create")
    @ResponseBody
    public Object create(@RequestBody Map map) {
        ExpSendOrder order = expSendService.newInstance(map);
        Long expId = order.getExpId();
        if(hasEmptyString(expId)) return fail("快递必选的哦~");
        ExpBrand expBrand = expBrandService.findById(expId);
        order.setExpName(expBrand.getExpName());

        if(hasEmptyString(order.getFetchAddr(),order.getFetchPhone(),order.getFetchName())){
            return fail("这几项都是必填项哦~");
        }

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
        return success(order, "/user/unified/detail?unifiedId=" + order.getId());
    }
}
