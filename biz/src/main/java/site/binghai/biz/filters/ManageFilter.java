package site.binghai.biz.filters;

import site.binghai.lib.inters.BaseInterceptor;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class ManageFilter extends BaseInterceptor {
    private static final String tag = "_SYS_MANAGER_";

    @Override
    protected String getRedirectUrl(HttpSession session, HttpServletRequest request) {
        return "/mloginPage#mlogin";
    }

    @Override
    protected String getFilterTag(HttpSession session) {
        return tag;
    }
}
