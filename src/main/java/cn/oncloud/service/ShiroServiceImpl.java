package cn.oncloud.service;

import cn.oncloud.mapper.MenuMapper;
import cn.oncloud.mapper.UserMapper;
import cn.oncloud.pojo.Menu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能描述：实现类
 *
 * @author YYY
 */
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    MenuMapper menuMapper;

    @Autowired
    UserMapper userMapper;

    public static final int SUPER_ADMIN = 1;

    @Override
    public Set<String> getUserPermissions(long userId) {
        // 用于存放权限字段
        List<String> permsList;
        // 系统管理员，拥有最高权限
        if (userId == SUPER_ADMIN) {
            List<Menu> menuList = menuMapper.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for (Menu menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = userMapper.queryAllPerms(userId);
        }
        //用户权限列表，把权限字段存入到set集合中
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }
}
