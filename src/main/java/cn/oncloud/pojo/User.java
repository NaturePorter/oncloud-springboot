package cn.oncloud.pojo;

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
    private String username;
    private String password;
    private String role;
    private String state;
    /**
     * 踩了个坑，数据库中的delete是个关键字，所以字段名不能使用delete
     */
    private String isdelete;

}
