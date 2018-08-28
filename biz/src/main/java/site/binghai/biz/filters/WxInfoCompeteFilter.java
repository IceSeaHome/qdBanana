package site.binghai.biz.filters;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import site.binghai.biz.utils.MockUtil;
import site.binghai.lib.entity.WxUser;
import site.binghai.lib.inters.BaseInterceptor;
import site.binghai.lib.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class WxInfoCompeteFilter extends HandlerInterceptorAdapter {
    private static final String tag = "_WX_USER_";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object obj = session.getAttribute(tag);
        if (obj == null) return false;

        WxUser user = (WxUser) obj;
        if (StringUtils.isBlank(user.getUserName()) ||
                StringUtils.isBlank(user.getPhone()) ||
                StringUtils.isBlank(user.getUsuallyAddress())) {
            response.sendRedirect("/wx/initUserInfo");
            return false;
        }
        return true;
    }
}
