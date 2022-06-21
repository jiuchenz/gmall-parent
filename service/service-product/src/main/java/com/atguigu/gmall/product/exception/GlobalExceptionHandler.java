package com.atguigu.gmall.product.exception;


import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result handlebizException(GmallException e){
        Result result = new Result();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        result.setData("");
        return result;
    }


    @ExceptionHandler
    public Result handleOtherException(Exception e){
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;
    }
}
