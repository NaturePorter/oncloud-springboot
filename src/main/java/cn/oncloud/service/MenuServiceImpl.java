package cn.oncloud.service;

import cn.oncloud.mapper.MenuMapper;
import cn.oncloud.pojo.Menu;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author YYY
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
