package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户与角色对应关系
 * @author YYY
 */
@Data
@TableName("oc_role_menu")
public class RoleMenu implements Serializable {
    private static final long serialVersionUID = 4916519954666092495L;

    /**
     * 角色菜单表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色Id
     */
    private Long roleId;
    /**
     * 菜单Id
     */
    private Long menuId;
}
