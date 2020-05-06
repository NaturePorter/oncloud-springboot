package cn.oncloud.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BussinessException extends RuntimeException{

    private int code;
    private String message;

    public BussinessException(Integer errorCode, String message) {
        this.code = errorCode;
        this.message = message;
    }
}