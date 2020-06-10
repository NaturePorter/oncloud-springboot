package cn.oncloud.service;

import cn.oncloud.mapper.DocumentMapper;
import cn.oncloud.entity.Document;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public IPage<Document> auditselectByDocname(Page<Document> page, String docName) {
        return documentMapper.auditselectByDocname(page, docName);
    }

    @Override
    public Integer updateDocAuditById(Integer id, Integer audit) {
        return documentMapper.updateUserStateById(id, audit);
    }
}
