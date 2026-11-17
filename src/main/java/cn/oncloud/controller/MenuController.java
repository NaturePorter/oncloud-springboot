package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.entity.Menu;
import cn.oncloud.entity.User;
import cn.oncloud.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YYY
 */
@RestController
@RequestMapping("/api")
public class MenuController extends AbstractController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/menu")
    @WebLog("拉取菜单")
    public ResponseEntity<ResultBean> menu() {
        User user = getUser();
        List<Menu> menuList = menuService.getUserMenuList(user.getId());
        return ResponseEntity.ok(ResultBean.ok(ResultConst.GET_MENU_SUCC, menuList));
    }
}
