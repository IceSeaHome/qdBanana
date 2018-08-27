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
    public String index(ModelMap map){
        return "userIndex";
    }
    @GetMapping("myInfo")
    public Object myInfo() {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        return success(wxUser, null);
    }

    @PostMapping("update")
    public Object update(@RequestBody Map map) {
        WxUser wxUser = getSessionPersistent(WxUser.class);
        try {
            wxUser = wxUserService.updateAndSave(wxUser, map);
            persistent(wxUser);
        } catch (Exception e) {
            logger.error("user update his info error", e);
            return fail(e.getMessage());
        }
        return success(wxUser, null);
    }
}
