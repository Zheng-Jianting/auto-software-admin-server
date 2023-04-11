package com.zhengjianting.autosoftware.common.exception;

import com.zhengjianting.autosoftware.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回状态码400
    @ExceptionHandler(value = RuntimeException.class) // 先处理大范围的异常, 大部分是运行时异常
    public Result handler(RuntimeException e) {
        log.error("运行时异常：" + e.getMessage());
        return Result.fail(e.getMessage());
    }

    // 捕获权限不足异常
    @ResponseStatus(HttpStatus.FORBIDDEN) // 权限不足403, 禁止访问
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result handler(AccessDeniedException e) {
        log.error("权限不足异常：" + e.getMessage());
        return Result.fail(403, e.getMessage());
    }

    // 捕获IllegalArgumentException异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常：" + e.getMessage());
        return Result.fail(e.getMessage());
    }

    // 捕获实体校验异常
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();
        log.error("实体校验异常：" + objectError.getDefaultMessage());
        return Result.fail(objectError.getDefaultMessage());
    }
}
