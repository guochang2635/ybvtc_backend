package com.ruralmedical.backend.exception;

import com.ruralmedical.backend.pojo.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerAdvice {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerAdvice.class);

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseMessage<Object>> handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        String message;
        System.out.println(e instanceof RuntimeException);
        if (e instanceof RuntimeException)
            message = e.getMessage();
        else
            message = "服务器内部错误";
        logger.error("统一处理：{} {} {} {}", request.getMethod(), request.getRequestURI(),e.getClass().getName(), e.getMessage());
        return new ResponseEntity<>(ResponseMessage.fail(500, message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ResponseMessage<Object>> handleUsernameNotFoundException(UsernameNotFoundException e, HttpServletRequest request, HttpServletResponse response) {
        logger.error("用户名不存在：{} {} {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return new ResponseEntity<>(ResponseMessage.fail(500, "用户不存在"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
