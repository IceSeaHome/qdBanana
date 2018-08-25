package site.binghai.biz.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.binghai.biz.filters.WxLoginFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/user/my/expTake").setViewName("expTake");
        registry.addViewController("/user/my/expSend").setViewName("expTake");
        registry.addViewController("/user/my/orders").setViewName("orders");
        registry.addViewController("/user/my/myCenter").setViewName("myCenter");
        registry.addViewController("/user/my/modifyMyInfo").setViewName("myCenter");
        registry.addViewController("/user/my/orderDetail").setViewName("orderDetail");
    }

    @Bean
    public WxLoginFilter wxLoginFilter() {
        return new WxLoginFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wxLoginFilter()).addPathPatterns("/user/**");
    }
}