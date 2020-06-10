package cn.oncloud.service;

import cn.oncloud.entity.Document;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 余弘洋
 */
public interface DocumentService extends IService<Document> {

    IPage<Document> selectByDocname(Page<Document> page, String docName, Long uid);

    IPage<Document> hallselectByDocname(Page<Document> page, String docName);

    Integer updateDocSharedById(Integer id, Integer shared);

    IPage<Document> auditselectByDocname(Page<Document> page, String docName);

    Integer updateDocAuditById(Integer id, Integer audit);
}
