package cn.oncloud.mapper;

import cn.oncloud.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author 余弘洋
 * 描述：mapper接口类
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select({"<script>",
            "select u.id, u.username, u.role, u.state",
            "from oc_user u where u.isdelete = '0'",
            "<if test='userName != null and userName != \"\"'>",
            "and u.username like concat('%', #{userName}, '%')",
            "</if>",
            "order by u.id asc",
            "</script>"})
    IPage<User> selectByUsername(Page<User> page, @Param("userName") String userName);

    @Update("update oc_user set state = #{state} where id = #{id} and isdelete = '0'")
    Integer updateUserStateById(@Param("id") Long id, @Param("state") Integer state);

    /**
     * 查询用户的所有权限
     */
    List<String> queryAllPerms(@Param("userId") Long userId);

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(@Param("userId") Long userId);
}
