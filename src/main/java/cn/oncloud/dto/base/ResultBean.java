package cn.oncloud.dto.base;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 余弘洋
 */
@Data
@NoArgsConstructor
public class ResultBean<T> {

    private ResultMeta meta;
    private T data;

    public static ResultBean ok(String msg){
        return new ResultBean(200, msg);
    }

    public static ResultBean ok(String msg, Object data){
        return new ResultBean(200, msg,data);
    }

    public static ResultBean ok(Object data){
        return new ResultBean(200, "执行成功",data);
    }

    public static ResultBean ok(){
        return new ResultBean(200, "执行成功");
    }

    public static ResultBean error(Integer code, String msg){
        return new ResultBean(code, msg);
    }

    private ResultBean(Integer code, String msg, Object data) {
        this.meta = ResultMeta.build(code, msg);
        this.data = (T)data;
    }

    private ResultBean(Integer code, String msg) {
        this.meta = ResultMeta.build(code, msg);
    }


}
