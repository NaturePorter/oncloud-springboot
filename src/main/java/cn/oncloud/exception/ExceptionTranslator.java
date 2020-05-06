package cn.oncloud.exception;

import cn.oncloud.dto.base.ResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionTranslator {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionTranslator.class);

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<ResultBean> processConcurencyError(ConcurrencyFailureException ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ResultBean> processParameterizedValidationError(TypeMismatchException ex) {
        logger.error(ex.getMessage(), ex);
        String valueType = ex.getValue().getClass().getSimpleName().toLowerCase();
        String rvalueType = ex.getRequiredType().getSimpleName().toLowerCase();
        String message = String.format("parameter's type is mistake, value is %s, value's type is %s, required type is %s",
                ex.getValue(), valueType, rvalueType);
        return processException(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultBean> processRequestParameterError(MissingServletRequestParameterException ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultBean> processBindValidError(BindException e){
        logger.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        return processException(HttpStatus.BAD_REQUEST, fieldError.getField() + ":" + fieldError.getDefaultMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validError(MethodArgumentNotValidException e){
        logger.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        return processException(HttpStatus.BAD_REQUEST, fieldError.getField() + ":" + fieldError.getDefaultMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultBean> processAccessDeniedError(AccessDeniedException ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResultBean> processMethodNotSupportedError(HttpRequestMethodNotSupportedException ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ResultBean> processCustomError(BussinessException ex) {
        logger.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ResultBean.error(ex.getCode(), ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultBean> processRuntimeError(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 不使用restful格式返回
     */
    private ResponseEntity<ResultBean> processException(HttpStatus httpStatus){
        return new ResponseEntity<>(ResultBean.error(httpStatus.value(), httpStatus.getReasonPhrase()), HttpStatus.OK);
    }

    private ResponseEntity<ResultBean> processException(HttpStatus httpStatus, String message){
        return new ResponseEntity<>(ResultBean.error(httpStatus.value(),message),
                HttpStatus.OK);
    }
}