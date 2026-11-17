package cn.oncloud.service;

import cn.oncloud.common.utils.BCrypt;
import cn.oncloud.common.utils.Constant;
import cn.oncloud.common.utils.JwtUtil;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.entity.User;
import cn.oncloud.exception.BussinessException;
import cn.oncloud.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author 余弘洋
 * 描述：用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 允许分配的角色
     */
    private static final Set<String> ROLES = Set.of(
            Constant.ROLE_SUPER_ADMIN, Constant.ROLE_ADMIN, Constant.ROLE_USER);

    private final TokenService tokenService;

    public UserServiceImpl(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public String login(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "用户名和密码不能为空");
        }
        User user = getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUsername, username.trim())
                .eq(User::getIsdelete, "0"));
        if (user == null || !matches(password, user.getPassword())) {
            throw new BussinessException(HttpStatus.UNAUTHORIZED.value(), ResultConst.INVALID_PASSWORD);
        }
        if (user.getState() != null && user.getState() == 0) {
            throw new BussinessException(HttpStatus.FORBIDDEN.value(), "账号已被停用，请联系管理员");
        }
        upgradePasswordIfNeeded(user, password);

        String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), user.getUsername(), null);
        tokenService.save(jwt, user, JwtUtil.JWT_TTL);
        return jwt;
    }

    /**
     * 校验密码：兼容历史明文密码，登录成功后自动升级为 BCrypt
     */
    private boolean matches(String rawPassword, String storedPassword) {
        if (StringUtils.isBlank(storedPassword)) {
            return false;
        }
        if (storedPassword.startsWith("$2")) {
            return BCrypt.checkpw(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private void upgradePasswordIfNeeded(User user, String rawPassword) {
        String stored = user.getPassword();
        if (stored != null && !stored.startsWith("$2")) {
            User update = new User();
            update.setId(user.getId());
            update.setPassword(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
            updateById(update);
            user.setPassword(update.getPassword());
        }
    }

    @Override
    public IPage<User> selectByUsername(Page<User> page, String userName) {
        return baseMapper.selectByUsername(page, StringUtils.trimToNull(userName));
    }

    @Override
    public Integer updateUserStateById(Long id, Integer state) {
        if (state == null || (state != 0 && state != 1)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "用户状态不合法");
        }
        return baseMapper.updateUserStateById(id, state);
    }

    @Override
    public void addUser(User user) {
        if (user == null || StringUtils.isAnyBlank(user.getUsername(), user.getPassword())) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "用户名和密码不能为空");
        }
        String username = user.getUsername().trim();
        if (username.length() < 3 || username.length() > 10) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "用户名长度需在3到10个字符之间");
        }
        if (user.getPassword().length() < 5 || user.getPassword().length() > 15) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "密码长度需在5到15个字符之间");
        }
        if (getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username)) != null) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), ResultConst.USER_NAME_EXIST);
        }
        User target = new User();
        target.setUsername(username);
        target.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        target.setRole(StringUtils.defaultIfBlank(user.getRole(), Constant.ROLE_USER));
        target.setState(1);
        target.setIsdelete("0");
        target.setMobile(user.getMobile());
        target.setEmail(user.getEmail());
        save(target);
    }

    @Override
    public void updateUserRole(Long id, String role) {
        if (!ROLES.contains(role)) {
            throw new BussinessException(HttpStatus.BAD_REQUEST.value(), "角色不存在");
        }
        User user = getById(id);
        if (user == null) {
            throw new BussinessException(HttpStatus.NOT_FOUND.value(), "用户不存在");
        }
        User update = new User();
        update.setId(id);
        update.setRole(role);
        updateById(update);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }
}
