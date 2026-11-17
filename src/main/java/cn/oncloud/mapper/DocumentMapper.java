package cn.oncloud.mapper;

import cn.oncloud.entity.Document;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 余弘洋
 * 功能描述：文档mapper接口类
 */
@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    /**
     * 我的文档
     */
    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.isshared, d.isaudit, d.uid",
            "from oc_document d where d.isdelete = 0 and d.uid = #{uid}",
            "<if test='docName != null and docName != \"\"'>",
            "and d.docname like concat('%', #{docName}, '%')",
            "</if>",
            "order by d.uploadtime desc, d.id desc",
            "</script>"})
    IPage<Document> selectByDocname(Page<Document> page,
                                    @Param("docName") String docName,
                                    @Param("uid") Long uid);

    @Update("update oc_document set isshared = #{shared} where id = #{id} and isdelete = '0'")
    Integer updateDocSharedById(@Param("id") Long id, @Param("shared") Integer shared);

    /**
     * 文档大厅：只展示已共享且已审核通过的文档
     */
    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.uid",
            "from oc_document d where d.isdelete = 0 and d.isshared = '1' and d.isaudit = '1'",
            "<if test='docName != null and docName != \"\"'>",
            "and d.docname like concat('%', #{docName}, '%')",
            "</if>",
            "order by d.uploadtime desc, d.id desc",
            "</script>"})
    IPage<Document> hallselectByDocname(Page<Document> page, @Param("docName") String docName);

    /**
     * 待审核文档
     */
    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.uid, d.isaudit",
            "from oc_document d where d.isdelete = 0 and d.isaudit = '0'",
            "<if test='docName != null and docName != \"\"'>",
            "and d.docname like concat('%', #{docName}, '%')",
            "</if>",
            "order by d.uploadtime desc, d.id desc",
            "</script>"})
    IPage<Document> auditselectByDocname(Page<Document> page, @Param("docName") String docName);

    @Update("update oc_document set isaudit = #{audit} where id = #{id} and isdelete = '0'")
    Integer updateDocAuditById(@Param("id") Long id, @Param("audit") Integer audit);

    @Update("update oc_document set downloadfrequency = downloadfrequency + 1 where id = #{id}")
    Integer increaseDownloadFrequency(@Param("id") Long id);
}
