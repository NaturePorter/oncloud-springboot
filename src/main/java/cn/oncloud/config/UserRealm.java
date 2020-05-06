package cn.oncloud.config;

import cn.oncloud.pojo.User;
import cn.oncloud.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 余弘洋
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    /**
     * 执行授权逻辑
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("执行了=》授权doGetAuthorizationInfo（）");
        return null;
    }

    /**
     * 执行认证逻辑
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("执行了=》认证doGetAuthenticationInfo（）");
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        //连接真实数据库
        LambdaQueryWrapper<User> queryWrapper =
                Wrappers.<User>lambdaQuery().eq(User::getUsername, ((UsernamePasswordToken) token).getUsername());
        User user = userService.getOne(queryWrapper);

        if (null == user) {
            return null;
        }
        //密码认证由shiro来做
        SimpleAuthenticationInfo simpleAuthenticationInfo =
                new SimpleAuthenticationInfo(user, user.getPassword(), "");
        return simpleAuthenticationInfo;
    }
}
