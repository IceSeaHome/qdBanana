package site.binghai.biz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.binghai.lib.config.IceConfig;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.service.BaseService;
import site.binghai.lib.service.dao.WxUserDao;

import java.util.List;

@Service
public class WxUserService extends BaseService<WxUser> {
    @Autowired
    private IceConfig iceConfig;
    @Autowired
    private WxUserDao wxUserDao;

    public WxUser findByOpenId(String openId) {
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        return queryOne(wxUser);
    }

    public WxUser newUser(String openId) {
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        wxUser.setAvatar(iceConfig.getDefaultAvatarUrl());
        return save(wxUser);
    }

    public List<WxUser> search(String search) {
        return null;
    }

    @Override
    protected JpaRepository<WxUser, Long> getDao() {
        return wxUserDao;
    }
}
