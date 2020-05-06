package cn.oncloud.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 余弘洋
 * 描述：字典实体类
 */
@Data
@TableName("oc_dict")
public class Dict implements Serializable {
    private static final long serialVersionUID = -3503003438195601986L;

    @TableId(type = IdType.AUTO)
    private int id;
    private String code;
    private String type;
    private String value;
    private String description;
}
