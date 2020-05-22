package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.pojo.User;
import cn.oncloud.service.UserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 余弘洋
 * 描述：用户功能类
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    @WebLog("登录接口")
    public ResponseEntity login(String username, String password) {
        String jwt = userService.login(username, password);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.LOGIN_SUCC, new JSONObject().fluentPut("token", jwt)),
                HttpStatus.OK);
    }

    @GetMapping("/user")
    @WebLog("首页用户信息接口")
    public ResponseEntity home(@RequestHeader("token") String token){
        System.out.println("开始请求用户信息");
        String strJson = stringRedisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(strJson, User.class);
        user.setPassword("");
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_USER_SUCC, user), HttpStatus.OK);
    }

    @GetMapping("/userlist")
    @WebLog("用户列表接口")
    public ResponseEntity userList(@RequestParam(name = "query", required = false) String userName,
                                   @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                   @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                   @RequestHeader("token") String token) {

        IPage<User> page = userService.selectByUsername(new Page<>(pageNum, pageSize), userName);
        JSONObject resp = new JSONObject().fluentPut("total", page.getTotal());
        resp.put("users", page.getRecords());
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_USERS_SUCC, resp), HttpStatus.OK);
    }

    @PutMapping("/userlist/{id}/state/{state}")
    @WebLog("修改用户状态接口")
    public ResponseEntity updateState(@PathVariable Integer id, @PathVariable Integer state) {
        userService.updateUserStateById(id, state);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.UPD_USER_STATUS_SUCC), HttpStatus.OK);
    }

    @GetMapping("/checkusername")
    @WebLog("验证用户名是否存在")
    public ResponseEntity checkUserName(String username) {
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, username);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            return new ResponseEntity<>(ResultBean.ok(ResultConst.USER_NAME_AVAILABLE), HttpStatus.OK);
        }
        return new ResponseEntity<>(ResultBean.error(201, "用户名已存在"), HttpStatus.OK);
    }

    @PostMapping("/adduser")
    @WebLog("添加用户接口")
    public ResponseEntity addUser(User user){
        userService.addUser(user);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.ADD_USER_SUCC), HttpStatus.OK);
    }


    @GetMapping("/userlist/{id}")
    @WebLog("查询用户信息接口")
    public ResponseEntity findById(@PathVariable Integer id){
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getId, id);
        User user = userService.getOne(queryWrapper);
        /** 将用户密码置空 */
        user.setPassword(null);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.GET_USER_SUCC, user), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUserByid/{id}")
    @WebLog("根据ID删除用户接口")
    public ResponseEntity deleteUserByid(@PathVariable Integer id){
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getId, id);
        User user = userService.getOne(queryWrapper);
        user.setIsdelete("1");
        userService.updateById(user);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.DEL_USER_SUCC), HttpStatus.OK);
    }

    @PutMapping("/edituserrole/{id}/role/{role}")
    @WebLog("根据ID修改用户角色接口")
    public ResponseEntity editUserRole(@PathVariable Integer id, @PathVariable String role){
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getId, id);
        User user = userService.getOne(queryWrapper);
        user.setRole(role);
        userService.updateById(user);
        return new ResponseEntity<>(ResultBean.ok(ResultConst.UPD_ROLE_SUCC), HttpStatus.OK);
    }
}
