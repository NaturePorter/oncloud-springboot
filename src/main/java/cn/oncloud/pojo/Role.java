package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("oc_role")
public class Role implements Serializable {
    private static final long serialVersionUID = 6849986238983864999L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String rolename;
    private Date createtime;
    private String description;
}
