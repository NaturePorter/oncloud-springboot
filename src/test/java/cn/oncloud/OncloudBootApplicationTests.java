package cn.oncloud;

import cn.oncloud.common.utils.BCrypt;
import cn.oncloud.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 不依赖数据库和 Redis 的基础单元测试
 */
class OncloudBootApplicationTests {

    @Test
    void bcryptShouldHashAndVerifyPassword() {
        String hash = BCrypt.hashpw("admin", BCrypt.gensalt());
        assertTrue(BCrypt.checkpw("admin", hash));
        assertFalse(BCrypt.checkpw("wrong", hash));
    }

    @Test
    void jwtShouldBeParsedWithSubject() {
        String token = JwtUtil.createJWT("id-1", "admin", null);
        Claims claims = JwtUtil.parseJWT(token);
        assertEquals("admin", claims.getSubject());
    }
}
