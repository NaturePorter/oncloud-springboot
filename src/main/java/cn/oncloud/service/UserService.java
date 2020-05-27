package cn.oncloud.service;

import cn.oncloud.pojo.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 余弘洋
 * 描述：Service接口类
 */
public interface UserService extends IService<User> {

    /**
     * 登录方法
     * @param username
     * @param password
     * @return 返回登录生成的token令牌
     */
    String login(String username, String password);

    /**
     * 查询用户列表
     * @param page 分页对象
     * @param userName 用户名
     * @return 返回已分页的列表
     */
    IPage<User> selectByUsername(Page<User> page, String userName);

    /**
     * 根据id修改用户state
     * @param id 用户id
     * @param state 用户状态
     * @return 返回成功与否
     */
    Integer updateUserStateById(Integer id, Integer state);

    /**
     * 添加用户
     * @param user 用户对象
     * @return 返回是否成功
     */
    void addUser(User user);

    List<Long> queryAllMenuId(Long userId);
}
