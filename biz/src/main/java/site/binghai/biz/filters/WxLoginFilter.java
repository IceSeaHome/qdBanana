package site.binghai.biz.filters;

import site.binghai.lib.inters.BaseInterceptor;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class WxLoginFilter extends BaseInterceptor {
    private static final String tag = "_WX_USER_";

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return "/wx/wxLogin?openId=LOGIN&validate=LOGIN&backUrl=" + UrlUtil.getFullUrl(request);
    }

    @Override
    protected String getFilterTag(HttpSession session) {
//        if(session.getAttribute(tag) == null){
//            //todo 测试逻辑
//            session.setAttribute(tag,MockUtil.mockWxUser());
//        }
        return tag;
    }
}
