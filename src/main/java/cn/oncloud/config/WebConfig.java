package cn.oncloud.config;

import cn.oncloud.util.CustomDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 余弘洋
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 注册字典
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomDialect customDialect() {
        return new CustomDialect("Dict Dialect");
    }

    /**
     * CORS解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("PUT", "DELETE", "GET", "POST")
                .allowedHeaders("*")
                .exposedHeaders("access-control-allow-headers",
                        "access-control-allow-methods",
                        "access-control-allow-origin",
                        "access-control-max-age",
                        "X-Frame-Options",
                        "Authorization")
                .allowCredentials(false).maxAge(3600);
    }

}
