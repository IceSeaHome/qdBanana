package site.binghai.biz.service;

import org.springframework.stereotype.Service;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;

@Service
public class WxUserService extends BaseService<WxUser> {

    public WxUser findByOpenId(String openId) {
        return null;
    }

    public WxUser newUser(String openId) {
        return null;
    }
}
