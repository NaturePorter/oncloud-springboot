package cn.oncloud.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 余弘洋
 */
@Configuration
public class ShiroConfig {

    /**
     * ShiroFilterFactoryBean
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("securityManager") DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //添加内置过滤器
        /**
         * 常用过滤器
         * anon:无需认证可以访问
         * authc:必须认证 才可以访问
         * user:如果使用rememberMe的功能才能访问
         * perms:该资源必须得到资源权限才可以访问
         * role:该资源必须得到角色权限才可以访问
         */
        Map<String, String> filterMap = new LinkedHashMap<>();
        /*//也可以使用统配的方法
        filterMap.put("/authc/*", "authc");
        //给验证码开放权限
        filterMap.put("/vcode", "anon");
        filterMap.put("/toregister", "anon");
        //修改默认登录页面
        shiroFilterFactoryBean.setLoginUrl("/tologin");*/
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * DefaultWebSecurityManager
     * @param userRealm
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

    /**
     * Realm
     * @return
     */
    @Bean(name = "userRealm")
    public UserRealm getRealm() {
        return new UserRealm();
    }

    /**
     * 整合shiroDialect
     *
     * @return
     */
    @Bean
    public ShiroDialect getShiroDialect() {
        return new ShiroDialect();
    }

}
