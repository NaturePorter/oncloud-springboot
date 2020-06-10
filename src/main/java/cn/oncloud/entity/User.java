package cn.oncloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 余弘洋
 * 描述：用户实体类
 */
@Data
/**如果数据库表和实体类名不一样，可使用这个标签匹配*/
@TableName("oc_user")
public class User implements Serializable {

    private static final long serialVersionUID = 636822119731277687L;
    /**设置数据库id自增*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 联系电话
     */
    private String mobile;
    /**
     * 邮箱地址
     */
    private String email;
    /**
     * 用户角色
     */
    private String role;
    /**
     * 用户状态
     * 正常：0
     * 禁用：1
     */
    private Integer state;
    /**
     * 踩了个坑，数据库中的delete是个关键字，所以字段名不能使用delete
     */
    private String isdelete;

}
