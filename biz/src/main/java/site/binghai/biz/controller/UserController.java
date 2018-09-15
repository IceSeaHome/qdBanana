package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.WxUser;

import java.util.Map;

@Controller
@RequestMapping("/user/my/")
public class UserController extends BaseController {
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("index")
    public String index(ModelMap map) {
        map.put("user", getSessionPersistent(WxUser.class));
        return "userIndex";
    }

    @GetMapping("myInfo")
    public String myInfo(ModelMap map) {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        wxUser = wxUserService.findById(wxUser.getId());
        persistent(wxUser);
        map.put("user", wxUser);
        return "myInfo";
    }

    @PostMapping("update")
    @ResponseBody
    public Object update(@RequestBody Map map) {
        WxUser user = getSessionPersistent(WxUser.class);
        String userName = getString(map, "userName");
        String phone = getString(map, "phone");
        String usuallyAddress = getString(map, "usuallyAddress");

        if (hasEmptyString(userName, phone, usuallyAddress)) {
            return fail("所有信息都是必填哦");
        }

        user.setUserName(userName);
        user.setPhone(phone);
        user.setUsuallyAddress(usuallyAddress);

        wxUserService.update(user);

        return success(null, "index");
    }
}
