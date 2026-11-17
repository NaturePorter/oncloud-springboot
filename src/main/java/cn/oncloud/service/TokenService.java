package cn.oncloud.service;

import cn.oncloud.common.utils.Constant;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.entity.User;
import cn.oncloud.exception.BussinessException;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录态服务：token 与用户信息统一从 Redis 读写
 *
 * @author 余弘洋
 */
@Service
public class TokenService {

    private final StringRedisTemplate redisTemplate;

    public TokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 保存登录态，过期时间与 JWT 保持一致
     */
    public void save(String token, User user, long ttlMillis) {
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setRole(user.getRole());
        safeUser.setState(user.getState());
        safeUser.setMobile(user.getMobile());
        safeUser.setEmail(user.getEmail());
        safeUser.setIsdelete(user.getIsdelete());
        redisTemplate.opsForValue().set(token, JSON.toJSONString(safeUser), ttlMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 按 token 取用户，取不到说明登录态失效
     */
    public User getUser(String token) {
        if (StringUtils.isBlank(token)) {
            throw new BussinessException(HttpStatus.UNAUTHORIZED.value(), ResultConst.INVALID_TOKEN);
        }
        String json = redisTemplate.opsForValue().get(token);
        if (StringUtils.isBlank(json)) {
            throw new BussinessException(HttpStatus.UNAUTHORIZED.value(), ResultConst.INVALID_TOKEN);
        }
        return JSON.parseObject(json, User.class);
    }

    public void remove(String token) {
        if (StringUtils.isNotBlank(token)) {
            redisTemplate.delete(token);
        }
    }

    /**
     * 是否为管理员（超级管理员或管理员）
     */
    public boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        return Constant.ROLE_SUPER_ADMIN.equals(user.getRole()) || Constant.ROLE_ADMIN.equals(user.getRole());
    }

    /**
     * 是否为文档所有者或管理员
     */
    public boolean canManage(User user, Long ownerId) {
        return isAdmin(user) || (user != null && user.getId() != null && user.getId().equals(ownerId));
    }
}
