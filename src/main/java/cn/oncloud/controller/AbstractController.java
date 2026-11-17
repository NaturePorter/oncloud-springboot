package cn.oncloud.controller;

import cn.oncloud.entity.User;
import cn.oncloud.exception.BussinessException;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.HttpStatus;

/**
 * 功能描述：controller的公共组件
 *
 * @author YYY
 */
public abstract class AbstractController {

    /**
     * 从 shiro 中获取当前登录用户
     */
    protected User getUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new BussinessException(HttpStatus.UNAUTHORIZED.value(), "登录已失效，请重新登录");
    }

    /**
     * 获取当前用户的Id
     */
    protected Long getUserId() {
        return getUser().getId();
    }
}
