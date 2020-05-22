package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author YYY
 */
@Data
@TableName("oc_role")
public class Role implements Serializable {
    private static final long serialVersionUID = 6849986238983864999L;
    /**
     * 角色Id
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;
    /**
     * 角色名称
     */
    @NotBlank(message="角色名称不能为空")
    private String roleName;
    /**
     *
     */
    private String remark;
    /**
     * 创建者ID
     */
    private Long createUserId;
    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist=false)
    private List<Long> menuIdList;
}
