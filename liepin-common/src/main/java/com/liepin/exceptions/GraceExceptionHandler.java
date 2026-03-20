package com.liepin.exceptions;

import com.liepin.grace.result.GraceJSONResult;
import com.liepin.grace.result.ResponseStatusEnum;
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 全局异常捕获是为了让 前端页面展示，微服务之间调用不需要异常捕获
 */
@ControllerAdvice
public class GraceExceptionHandler {

    // 测试全局异常是否能促发seata回滚
//    @ExceptionHandler(ArithmeticException.class)
//    @ResponseBody
//    public GraceJSONResult returnArithmeticException(ArithmeticException e) {
//        e.printStackTrace();
//        return GraceJSONResult.errorMsg(e.getMessage());
//    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.FILE_MAX_SIZE_500KB_ERROR);
    }
    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler({
            SignatureException.class // 签名异常
            ,
            ExpiredJwtException.class, // 过期异常
            UnsupportedJwtException.class,  // jwt不支持异常
            MalformedJwtException.class, // 畸形，被篡改
            io.jsonwebtoken.security.SignatureException.class
    })
    @ResponseBody
    public GraceJSONResult returnSignatureException(SignatureException e) {
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
    }

    /**
     * 参数校验的异常拦截，可以返回多个错误
     * 这个前端还得做二次处理。
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> errors = getErrors(result);
        return GraceJSONResult.errorMap(errors);
    }

    public Map<String, String> getErrors(BindingResult result) {

        Map<String, String> map = new HashMap<>();

        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError fe : errorList) {
            // 错误所对应的属性字段名
            String field = fe.getField();
            // 错误信息
            String message = fe.getDefaultMessage();

            map.put(field, message);
        }

        return map;
    }

}
