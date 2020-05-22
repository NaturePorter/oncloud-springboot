package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author YYY
 * 描述：保存token令牌的备用方案
 * 将token保存在mysql数据库中的实体类
 */
@Data
@TableName("oc_user_token")
public class UserToken implements Serializable {

    private static final long serialVersionUID = -8628657646171580423L;
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;
    /**
     * token令牌
     */
    private String token;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 令牌更新时间
     */
    private Date updateTime;
}
