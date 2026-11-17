package cn.oncloud.service;

import cn.oncloud.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 余弘洋
 * 描述：用户Service接口类
 */
public interface UserService extends IService<User> {

    /**
     * 登录，返回 token
     */
    String login(String username, String password);

    /**
     * 分页查询用户
     */
    IPage<User> selectByUsername(Page<User> page, String userName);

    /**
     * 启用/停用用户
     */
    Integer updateUserStateById(Long id, Integer state);

    /**
     * 新增用户
     */
    void addUser(User user);

    /**
     * 修改用户角色
     */
    void updateUserRole(Long id, String role);

    /**
     * 查询用户拥有的菜单ID
     */
    List<Long> queryAllMenuId(Long userId);
}
