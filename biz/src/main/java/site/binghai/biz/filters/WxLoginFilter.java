package site.binghai.biz.filters;

import site.binghai.biz.utils.MockUtil;
import site.binghai.lib.inters.BaseInterceptor;

import javax.servlet.http.HttpSession;


public class WxLoginFilter extends BaseInterceptor {
    private static final String tag = "WX_USER";

    @Override
    protected String getRedirectUrl(HttpSession session) {
        return null;
    }

    @Override
    protected String getFilterTag(HttpSession session) {
        if(session.getAttribute(tag) == null){
            //todo 测试逻辑
            session.setAttribute(tag,MockUtil.mockWxUser());
        }
        return tag;
    }
}
