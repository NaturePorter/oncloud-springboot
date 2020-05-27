package cn.oncloud.filter.oauth2;

import cn.oncloud.pojo.User;
import cn.oncloud.service.ShiroService;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 认证
 *
 * @author YYYY
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权
     * 功能描述：验证权限时调用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了=》授权doGetAuthorizationInfo（）");
        User user = (User)principals.getPrimaryPrincipal();
        System.out.println(user);
        //用户权限列表
        Set<String> permsSet = shiroService.getUserPermissions(user.getId());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证
     * 功能描述：登录时调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了=》认证doGetAuthenticationInfo（）");

        String accessToken = (String) token.getPrincipal();

        //根据accessToken，查询用户信息
        String strJson = stringRedisTemplate.opsForValue().get(accessToken);
        User user = JSON.parseObject(strJson, User.class);
        //token失效
        if(user == null){
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }
        //账号锁定
        if(user.getState() == 0){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, accessToken, getName());
        return info;
    }
}
