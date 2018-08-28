package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.SessionDataBundle;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.utils.MD5;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/wx/")
public class WxController extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("wxLogin")
    public String wxLogin(@RequestParam String openId, @RequestParam String validate, String backUrl) {

        if (backUrl != null) {
            setString2Session(SessionDataBundle.BACK_URL, backUrl);
            return "redirect:" + iceConfig.getWxAuthenticationUrl() + "?backUrl=" + iceConfig.getAppRoot() + "/wx/wxLogin";
        }

        if (!MD5.encryption(openId + iceConfig.getWxValidateMD5Key()).equals(validate)) {
            return "redirect:" + iceConfig.getWxAuthenticationUrl();
        }

        WxUser wxUser = wxUserService.findByOpenId(openId);
        if (wxUser == null) {
            wxUser = wxUserService.newUser(openId);
        }

        persistent(wxUser);

        backUrl = getStringFromSession(SessionDataBundle.BACK_URL);
        return backUrl == null ? "redirect:/" : "redirect:" + backUrl;
    }

    @GetMapping("initUserInfo")
    public String initUserInfo(HttpServletRequest request, ModelMap map) {
        WxUser user = getSessionPersistent(WxUser.class);
        if (user == null) {
            return "redirect:/wx/wxLogin?openId=LOGIN&validate=LOGIN&backUrl=" + UrlUtil.getFullUrl(request);
        }
        map.put("user", user);
        return "initUserInfo";
    }

    @PostMapping("updateInitUserInfo")
    @ResponseBody
    public Object updateInitUserInfo(@RequestBody Map map) {
        WxUser user = getSessionPersistent(WxUser.class);
        if (user == null) {
            return fail("登录失效!");
        }

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
        return success(null, "/");
    }
}
