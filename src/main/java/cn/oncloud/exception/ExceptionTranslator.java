package cn.oncloud.exception;

import cn.oncloud.dto.base.ResultBean;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
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
        Object value = ex.getValue();
        String message = String.format("参数类型不正确：%s", value);
        return processException(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultBean> processRequestParameterError(MissingServletRequestParameterException ex) {
        logger.warn(ex.getMessage());
        return processException(HttpStatus.BAD_REQUEST, "缺少参数：" + ex.getParameterName());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResultBean> processBindValidError(BindException e) {
        logger.warn(e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getField() + ":" + fieldError.getDefaultMessage();
        return processException(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultBean> validError(MethodArgumentNotValidException e) {
        logger.warn(e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null ? "参数校验失败" : fieldError.getField() + ":" + fieldError.getDefaultMessage();
        return processException(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 未登录 / 登录态失效
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ResultBean> processUnauthenticated(UnauthenticatedException ex) {
        logger.warn(ex.getMessage());
        return processException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录");
    }

    /**
     * 已登录但没有权限
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ResultBean> processAccessDeniedError(AuthorizationException ex) {
        logger.warn(ex.getMessage());
        return processException(HttpStatus.FORBIDDEN, "没有操作权限");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResultBean> processMethodNotSupportedError(HttpRequestMethodNotSupportedException ex) {
        logger.warn(ex.getMessage());
        return processException(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<ResultBean> processCustomError(BussinessException ex) {
        logger.warn(ex.getMessage());
        return new ResponseEntity<>(ResultBean.error(ex.getCode(), ex.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultBean> processRuntimeError(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return processException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ResultBean> processException(HttpStatus httpStatus) {
        return processException(httpStatus, httpStatus.getReasonPhrase());
    }

    private ResponseEntity<ResultBean> processException(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(ResultBean.error(httpStatus.value(), message), HttpStatus.OK);
    }
}
