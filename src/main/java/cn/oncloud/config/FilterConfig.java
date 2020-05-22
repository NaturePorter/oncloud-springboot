

package cn.oncloud.config;

import cn.oncloud.filter.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 *
 * @author YYYY
 */
@Configuration
public class FilterConfig {

    /*@Bean
    public FilterRegistrationBean authorizeFilter(){
        FilterRegistrationBean filter = new FilterRegistrationBean(new AuthorizeFilter());
        filter.addUrlPatterns("/api/*");
        filter.setName("authorizeFilter");
        filter.setOrder(1);
        return filter;
    }

    @Bean
    public FilterRegistrationBean xssFilter(){
        FilterRegistrationBean filter = new FilterRegistrationBean(new XssFilter());
        filter.addUrlPatterns("/api/*");
        filter.setName("xssFilter");
        filter.setOrder(2);
        return filter;
    }*/

    @Bean
    public FilterRegistrationBean shiroFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //该值缺省为false，表示生命周期由SpringApplicationContext管理，设置为true则表示由ServletContainer管理
        registration.addInitParameter("targetFilterLifecycle", "true");
        registration.setEnabled(true);
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(2);
        return registration;
    }

}
