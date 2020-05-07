package cn.oncloud;

import cn.oncloud.aspect.LogAspect;
import cn.oncloud.filter.AuthorizeFilter;
import cn.oncloud.filter.XssFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * 描述：项目启动类，程序入口
 * @author 余弘洋
 */
@SpringBootApplication
//扫描mp
@MapperScan("cn.oncloud.mapper")
public class OncloudBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(OncloudBootApplication.class, args);
    }

    @Bean
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
    }
}
