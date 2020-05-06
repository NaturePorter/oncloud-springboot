package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 余弘洋
 * 功能描述：文档实体类
 */
@Data
@TableName("oc_document")
public class Document implements Serializable {

    private static final long serialVersionUID = 6975962075120959289L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String docname;
    private String realname;
    private String perfix;
    private String docpath;
    private Long docsize;
    private String doclabel;
    private String docdescribe;
    private Long downloadfrequency;
    private Date uploadtime;
    private Long uid;
    private String isshared;
    private String isdelete;
    private String isaudit;
}
