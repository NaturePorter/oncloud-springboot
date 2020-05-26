package cn.oncloud.controller;


import cn.oncloud.pojo.User;
import org.apache.shiro.SecurityUtils;

/**
 * 功能描述：controller的公共组件
 * @author YYY
 */
public abstract class AbstractController {

    /**
     * 功能描述：从shiro中获取对象
     * @return
     */
    protected User getUser() {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 获取当前用户的Id
     * @return
     */
    protected Long getUserId() {
        return getUser().getId();
    }
}
