package cn.oncloud.service;

import java.util.Set;

/**
 * 功能描述：供shiro框架使用的
 * @author YYY
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);
}
