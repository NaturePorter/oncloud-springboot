package cn.oncloud.service;

import cn.oncloud.mapper.DocumentMapper;
import cn.oncloud.pojo.Document;
import cn.oncloud.pojo.User;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 余弘洋
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Autowired
    DocumentMapper documentMapper;

    @Override
    public IPage<Document> selectByDocname(Page<Document> page, String docName, Long uid) {
        return documentMapper.selectByDocname(page, docName, uid);
    }

    @Override
    public IPage<Document> hallselectByDocname(Page<Document> page, String docName) {
        return documentMapper.hallselectByDocname(page, docName);
    }

    @Override
    public Integer updateDocSharedById(Integer id, Integer shared) {
        return documentMapper.updateDocSharedById(id, shared);
    }
}
