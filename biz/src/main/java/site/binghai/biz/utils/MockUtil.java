package site.binghai.biz.utils;

import site.binghai.lib.entity.WxUser;

public class MockUtil {

    public static WxUser mockWxUser() {
        WxUser wxUser = new WxUser();
        wxUser.setId(999999999999L);
        wxUser.setAvatar("");
        wxUser.setPhone("18866668888");
        wxUser.setUserName("测试Mock");
        wxUser.setOpenId("FFFF-FFFF-FFFF-FFFF");
        wxUser.setUsuallyAddress("FUCKU");
        wxUser.setRefereeId(-1L);
        return wxUser;
    }
}
