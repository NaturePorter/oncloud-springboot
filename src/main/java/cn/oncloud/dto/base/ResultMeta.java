package cn.oncloud.dto.base;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultMeta {
    private Integer status;//返回码
    private String msg;//返回消息

    private ResultMeta(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static ResultMeta build(Integer status, String msg){
        return new ResultMeta(status, msg);
    }
}