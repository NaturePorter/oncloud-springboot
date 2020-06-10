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
    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.isshared, d.isaudit",
            "from oc_document d where d.isdelete = 0 and d.uid = ${uid}",
            "<if test='docName != null'>",
            "and d.docname like concat ('%','${docName}','%') ",
            "</if>",
            "</script>"})
    IPage<Document> selectByDocname(Page<Document> page, @Param(value = "docName") String docName, Long uid);

    @Update("UPDATE oc_document SET isshared = ${shared} WHERE id = ${id}")
    Integer updateDocSharedById(Integer id, Integer shared);

    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.uid ",
            "from oc_document d where d.isdelete = 0 and d.isshared = 1 and d.isaudit = 1",
            "<if test='docName != null'>",
            "and d.docname like concat ('%','${docName}','%') ",
            "</if>",
            "</script>"})
    IPage<Document> hallselectByDocname(Page<Document> page, String docName);

    @Select({"<script>",
            "select d.id, d.docname, d.docsize, d.doclabel, d.docdescribe, d.downloadfrequency, d.uploadtime, d.uid, d.isaudit ",
            "from oc_document d where d.isdelete = 0 and d.isaudit = 0",
            "<if test='docName != null'>",
            "and d.docname like concat ('%','${docName}','%') ",
            "</if>",
            "</script>"})
    IPage<Document> auditselectByDocname(Page<Document> page, String docName);

    @Update("UPDATE oc_document SET isaudit = ${audit} WHERE id = ${id}")
    Integer updateUserStateById(Integer id, Integer audit);
}
