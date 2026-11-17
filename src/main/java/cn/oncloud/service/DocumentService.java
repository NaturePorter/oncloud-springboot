package cn.oncloud.service;

import cn.oncloud.entity.Document;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 余弘洋
 */
public interface DocumentService extends IService<Document> {

    /**
     * 我的文档
     */
    IPage<Document> selectByDocname(Page<Document> page, String docName, Long uid);

    /**
     * 文档大厅
     */
    IPage<Document> hallselectByDocname(Page<Document> page, String docName);

    /**
     * 修改共享状态
     */
    Integer updateDocSharedById(Long id, Integer shared);

    /**
     * 待审核文档
     */
    IPage<Document> auditselectByDocname(Page<Document> page, String docName);

    /**
     * 审核文档
     */
    Integer updateDocAuditById(Long id, Integer audit);

    /**
     * 下载次数 +1
     */
    void increaseDownloadFrequency(Long id);
}
