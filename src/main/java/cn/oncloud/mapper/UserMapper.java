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
            "from oc_user u where u.isdelete = 0",
            "<if test='userName != null'>",
            "and u.username like concat ('%','${userName}','%') ",
            "</if>",
            "</script>"})
    IPage<User> selectByUsername(Page<User> page, @Param(value = "userName") String userName);


    @Update("UPDATE oc_user SET state = ${state} WHERE id = ${id}")
    Integer updateUserStateById(Integer id, Integer state);

    /**
     * 查询用户的所有权限
     * @param userId  用户ID
     */
    List<String> queryAllPerms(Long userId);

    List<Long> queryAllMenuId(Long userId);
}
