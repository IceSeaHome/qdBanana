package site.binghai.biz.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.binghai.biz.filters.WxInfoCompeteFilter;
import site.binghai.biz.filters.WxLoginFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/manage/index").setViewName("manage");
    }

    @Bean
    public WxLoginFilter wxLoginFilter() {
        return new WxLoginFilter();
    }

    @Bean
    public WxInfoCompeteFilter wxInfoCompeteFilter() {
        return new WxInfoCompeteFilter();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(wxLoginFilter()).addPathPatterns("/user/**");
        registry.addInterceptor(wxInfoCompeteFilter()).addPathPatterns("/user/**");
    }
}