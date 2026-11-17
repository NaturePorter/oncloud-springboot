package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.common.utils.Constant;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.entity.User;
import cn.oncloud.exception.BussinessException;
import cn.oncloud.service.TokenService;
import cn.oncloud.service.UserService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author 余弘洋
 * 描述：用户功能类
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @WebLog("登录接口")
    public ResponseEntity<ResultBean> login(@RequestParam String username, @RequestParam String password) {
        String jwt = userService.login(username, password);
        JSONObject data = new JSONObject().fluentPut("token", jwt);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.LOGIN_SUCC, data));
    }

    @PostMapping("/logout")
    @WebLog("退出登录接口")
    public ResponseEntity<ResultBean> logout(@RequestHeader("token") String token) {
        tokenService.remove(token);
        return ResponseEntity.ok(ResultBean.ok("退出登录成功"));
    }

    @GetMapping("/user")
    @WebLog("首页用户信息接口")
    public ResponseEntity<ResultBean> home(@RequestHeader("token") String token) {
        User user = tokenService.getUser(token);
        user.setPassword(null);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_USER_SUCC, user));
    }

    @GetMapping("/userlist")
    @WebLog("用户列表接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> userList(@RequestParam(name = "query", required = false) String userName,
                                               @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        IPage<User> page = userService.selectByUsername(new Page<>(pageNum, pageSize), userName);
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("users", page.getRecords());
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_USERS_SUCC, resp));
    }

    @PutMapping("/userlist/{id}/state/{state}")
    @WebLog("修改用户状态接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> updateState(@PathVariable Long id, @PathVariable Integer state,
                                                  @RequestHeader("token") String token) {
        checkNotSuperAdmin(id, token);
        userService.updateUserStateById(id, state);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.UPD_USER_STATUS_SUCC));
    }

    @GetMapping("/checkusername")
    @WebLog("验证用户名是否存在")
    public ResponseEntity<ResultBean> checkUserName(@RequestParam String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, username.trim());
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return ResponseEntity.ok(ResultBean.ok(ResultConst.USER_NAME_AVAILABLE));
        }
        return ResponseEntity.ok(ResultBean.error(HttpStatus.BAD_REQUEST.value(), "用户名已存在"));
    }

    @PostMapping("/adduser")
    @WebLog("添加用户接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> addUser(User user) {
        userService.addUser(user);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.ADD_USER_SUCC));
    }

    @GetMapping("/userlist/{id}")
    @WebLog("查询用户信息接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> findById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BussinessException(HttpStatus.NOT_FOUND.value(), "用户不存在");
        }
        user.setPassword(null);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_USER_SUCC, user));
    }

    @DeleteMapping("/deleteUserByid/{id}")
    @WebLog("根据ID删除用户接口")
    @RequiresRoles(value = {Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN}, logical = Logical.OR)
    public ResponseEntity<ResultBean> deleteUserByid(@PathVariable Long id,
                                                     @RequestHeader("token") String token) {
        checkNotSuperAdmin(id, token);
        User user = userService.getById(id);
        if (user == null) {
            throw new BussinessException(HttpStatus.NOT_FOUND.value(), "用户不存在");
        }
        User update = new User();
        update.setId(id);
        update.setIsdelete("1");
        userService.updateById(update);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.DEL_USER_SUCC));
    }

    @PutMapping("/edituserrole/{id}")
    @WebLog("根据ID修改用户角色接口")
    @RequiresRoles(Constant.ROLE_SUPER_ADMIN)
    public ResponseEntity<ResultBean> editUserRole(@PathVariable Long id, @RequestParam String role) {
        userService.updateUserRole(id, role);
        return ResponseEntity.ok(ResultBean.ok(ResultConst.UPD_ROLE_SUCC));
    }

    /**
     * 超级管理员账号不允许被停用或删除
     */
    private void checkNotSuperAdmin(Long id, String token) {
        User current = tokenService.getUser(token);
        if (Constant.SUPER_ADMIN == id) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "不允许操作超级管理员账号");
        }
        if (current.getId().equals(id) && Constant.ROLE_SUPER_ADMIN.equals(current.getRole())) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "不允许操作超级管理员账号");
        }
    }
}
