package site.binghai.biz.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.entity.ApiToken;
import site.binghai.biz.service.ApiTokenService;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.UnifiedOrder;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.UnifiedOrderService;
import site.binghai.lib.utils.TimeTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/private/")
public class ApiController extends BaseController {

    @Autowired
    private ApiTokenService apiTokenService;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private UnifiedOrderService unifiedOrderService;

    @GetMapping("userList")
    public Object userList(@RequestParam String scode, @RequestParam Integer page) {

        if (!checkToken(scode)) {
            return fail("秘钥不正确!");
        }


        List<WxUser> userList = wxUserService.findAll(page < 0 ? 0 : page, 100);
        JSONObject data = new JSONObject();

        data.put("list", userList);
        data.put("total", wxUserService.count());
        data.put("page", page);

        return data;
    }


    @GetMapping("orderList")
    public Object orderList(@RequestParam String scode, @RequestParam Integer page) {
        if (!checkToken(scode)) {
            return fail("秘钥不正确!");
        }

        List<UnifiedOrder> orders = unifiedOrderService.findAll(page < 0 ? 0 : page, 100);
        JSONObject data = new JSONObject();

        data.put("list", orders);
        data.put("total", wxUserService.count());
        data.put("page", page);

        return data;
    }


    public boolean checkToken(String tokenString) {
        if (StringUtils.isBlank(tokenString)) {
            return false;
        }

        Map<String, ApiToken> tokenMap = new HashMap<>();

        long count = apiTokenService.findAll(999)
                .stream().filter(v -> tokenString.equals(v.getToken()))
                .peek(v -> tokenMap.put(v.getToken(), v))
                .count();

        if (count <= 0) {
            return false;
        }

        ApiToken token = tokenMap.get(tokenString);
        logger.warn("userList invoked by {},token = {}", token.getHolderName(), token.getToken());
        token.setInvokeTimes(token.getInvokeTimes() == null ? 1 : token.getInvokeTimes() + 1);
        token.setLastInvokeTime(TimeTools.now());
        apiTokenService.update(token);

        return true;
    }
}
