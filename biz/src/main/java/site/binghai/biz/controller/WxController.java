package site.binghai.biz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.binghai.biz.service.WxUserService;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.controller.BaseController;
import site.binghai.lib.entity.SessionDataBundle;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.utils.MD5;

@RestController
@RequestMapping("/wx/")
public class WxController extends BaseController {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("wxLogin")
    public Object wxLogin(@RequestParam String openId, @RequestParam String validate, String backUrl) {

        if (backUrl != null) {
            setString2Session(SessionDataBundle.BACK_URL, backUrl);
            return "redirect:" + iceConfig.getWxAuthenticationUrl();
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
        //todo
        return backUrl == null ? "/" : backUrl;
    }
}
