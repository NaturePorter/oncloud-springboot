package cn.oncloud.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类
 *
 * @author 余弘洋
 */
public class JwtUtil {

    /**
     * token 有效期：2 小时
     */
    public static final long JWT_TTL = 2 * 60 * 60 * 1000L;

    /**
     * 签名密钥，生产环境请通过环境变量 ONCLOUD_JWT_SECRET 覆盖（长度至少 32 字节）
     */
    private static final String JWT_KEY = System.getenv().getOrDefault(
            "ONCLOUD_JWT_SECRET", "oncloud-document-manager-jwt-secret-key-2026");

    private JwtUtil() {
    }

    /**
     * 创建token
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        long ttl = ttlMillis == null ? JWT_TTL : ttlMillis;

        JwtBuilder builder = Jwts.builder()
                // 唯一ID
                .setId(id)
                // 主题，这里放用户名
                .setSubject(subject)
                .setIssuer("oc_admin")
                .setIssuedAt(now)
                .signWith(generalKey(), SignatureAlgorithm.HS256)
                .setExpiration(new Date(nowMillis + ttl));
        return builder.compact();
    }

    /**
     * 生成 HMAC-SHA256 密钥
     */
    public static SecretKey generalKey() {
        return Keys.hmacShaKeyFor(JWT_KEY.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析
     */
    public static Claims parseJWT(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(generalKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
