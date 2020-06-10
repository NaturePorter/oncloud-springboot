package cn.oncloud.mapper;

import cn.oncloud.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author YYY
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据父菜单，查询子菜单
     * @param parentId 父菜单ID
     */
    @Select("select * from oc_menu where parent_id = #{parentId} order by order_num asc")
    List<Menu> queryListParentId(@Param("parentId") Long parentId);
}
