package cn.oncloud.service;

import cn.oncloud.mapper.DictMapper;
import cn.oncloud.pojo.Dict;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
/**
 * @author 余弘洋
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
}
