package cn.oncloud.util;

import cn.oncloud.pojo.Dict;
import cn.oncloud.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 字典工具类
 * @author 余弘洋
 */
@Component
public class DictUtils {

    @Autowired
    private DictService service;
    private static DictService sysDictService;

    @PostConstruct //完成对service的注入
    private void init() {
        sysDictService = service;
    }

    /**
     * 获取字段值
     */
    public String getDictValue(String type, Integer code) {
        //缓存中数据，则db
        Dict dict = sysDictService.getOne(new QueryWrapper<Dict>().eq("type", type).eq("code", code));
        return dict.getValue();
    }

    /**
     * 获取字典value值
     */
    public String getCode(String type, String label) {
        //缓存中数据，则db
        Dict dict = sysDictService.getOne(new QueryWrapper<Dict>().eq("type", type).eq("label", label));
        return dict.getCode();
    }

    /**
     * 获取字典集合
     */
    public List<Dict> getDicList(String type) {
        return sysDictService.list(new QueryWrapper<Dict>().eq("type", type));
    }

}
