package cn.oncloud.filter.oauth2;

import cn.oncloud.entity.User;
import cn.oncloud.service.ShiroService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

/**
 * 认证
 *
 * @author YYYY
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    private final StringRedisTemplate stringRedisTemplate;
    private final ShiroService shiroService;

    public OAuth2Realm(StringRedisTemplate stringRedisTemplate, ShiroService shiroService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.shiroService = shiroService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }

    /**
     * 授权：验证权限时调用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        User user = (User) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (StringUtils.isNotBlank(user.getRole())) {
            info.setRoles(Collections.singleton(user.getRole()));
        }
        Set<String> permsSet = shiroService.getUserPermissions(user.getId());
        info.setStringPermissions(permsSet);
        return info;
    }

    /**
     * 认证：每次请求携带 token 时调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String accessToken = (String) token.getPrincipal();
        String strJson = stringRedisTemplate.opsForValue().get(accessToken);
        User user = StringUtils.isBlank(strJson) ? null : JSON.parseObject(strJson, User.class);
        //token失效
        if (user == null) {
            throw new IncorrectCredentialsException("token失效，请重新登录");
        }
        //账号锁定
        if (user.getState() != null && user.getState() == 0) {
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        return new SimpleAuthenticationInfo(user, accessToken, getName());
    }
}
