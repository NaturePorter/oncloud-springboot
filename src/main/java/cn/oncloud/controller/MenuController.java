package cn.oncloud.controller;

import cn.oncloud.aspect.WebLog;
import cn.oncloud.dto.base.ResultBean;
import cn.oncloud.dto.base.ResultConst;
import cn.oncloud.pojo.Menu;
import cn.oncloud.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class MenuController  extends AbstractController{

    @Autowired
    private MenuService menuService;

    @GetMapping("/menu")
    @WebLog("拉去菜单")
    public ResponseEntity menu(){
        List<Menu> menuList = menuService.getUserMenuList(getUserId());
        return new ResponseEntity(ResultBean.ok(ResultConst.GET_MENU_SUCC, menuList), HttpStatus.OK);
    }
}
