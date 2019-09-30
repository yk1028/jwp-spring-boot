package myblog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import support.version.BlogVersion;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    public static final String PREFIX_STATIC_RESOURCES = "/resources";

    @Autowired
    private BlogVersion version;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/css/**")
//                .addResourceLocations("classpath:/static/css/")
//                .setCacheControl(CacheControl.maxAge(31536000, TimeUnit.MILLISECONDS));
//
//
//        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/js/**")
//                .addResourceLocations("classpath:/static/js/")
//                .setCacheControl(CacheControl.noCache().cachePrivate());
//
        registry.addResourceHandler(PREFIX_STATIC_RESOURCES + "/" + version.getVersion() + "/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache().cachePrivate());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                response.setHeader("Cache-Control","no-cache, no-store");
                response.setHeader("Pragma","no-cache");
                response.setDateHeader("Expires",0);
            }
        });
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        Filter etagHeaderFilter = new ShallowEtagHeaderFilter();
        registration.setFilter(etagHeaderFilter);
        registration.addUrlPatterns(PREFIX_STATIC_RESOURCES + "/*");
        return registration;
    }
}
