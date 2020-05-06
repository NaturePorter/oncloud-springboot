package cn.oncloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author 余弘洋
 * 功能描述：swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {



    /**
     * 配置swagger的docket的bean实例
     * enable:是否启用swagger，false为不启用
     *
     * 我的swagger在生产环境
     * @return
     */
    @Bean
    public Docket docket(Environment environment) {
        //设置要显示的swagger环境
        Profiles profiles = Profiles.of("dev");
        //通过environment.acceptsProfiles判断是否处在dev环境
        boolean falg = environment.acceptsProfiles(profiles);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("余弘洋")
                .enable(falg)
                .select()
                /**
                 * any()扫描全部
                 * basePackage()扫描特定的包
                 * none()不扫描
                 * withClassAnnotation()扫描类上的注解，参数是一个注解的反射对象
                 * withMethodAnnotation()扫描方法上的注解
                 */
                .apis(RequestHandlerSelectors.basePackage("cn.oncloud.controller"))
                //过滤**路径
                .paths(PathSelectors.any())
                .build();
    }

    //作者信息
    public static final Contact CONTACT = new Contact("余弘洋", "", "879704116@qq.com");

    private ApiInfo apiInfo() {
        return new ApiInfo(
                //题目
                "毕设-文档管理系统",
                //描述
                "基于SpringBoot的文档管理系统设计与实现",
                //版本
                "1.0",
                //组织的url
                "urn:tos",
                //作者信息
                CONTACT,
                //Apache版本
                "Apache 2.0",
                //Apache网址
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());
    }

}
