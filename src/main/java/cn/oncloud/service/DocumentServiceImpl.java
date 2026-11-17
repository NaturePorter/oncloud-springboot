package cn.oncloud.service;

import cn.oncloud.entity.Document;
import cn.oncloud.mapper.DocumentMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author 余弘洋
 */
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public IPage<Document> selectByDocname(Page<Document> page, String docName, Long uid) {
        return baseMapper.selectByDocname(page, docName, uid);
    }

    @Override
    public IPage<Document> hallselectByDocname(Page<Document> page, String docName) {
        return baseMapper.hallselectByDocname(page, docName);
    }

    @Override
    public Integer updateDocSharedById(Long id, Integer shared) {
        return baseMapper.updateDocSharedById(id, shared);
    }

    @Override
    public IPage<Document> auditselectByDocname(Page<Document> page, String docName) {
        return baseMapper.auditselectByDocname(page, docName);
    }

    @Override
    public Integer updateDocAuditById(Long id, Integer audit) {
        return baseMapper.updateDocAuditById(id, audit);
    }

    @Override
    public void increaseDownloadFrequency(Long id) {
        baseMapper.increaseDownloadFrequency(id);
    }
}
