package top.yeonon.lmmall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.yeonon.lmmall.common.ServerResponse;

import java.util.Map;

/**
 * @Author yeonon
 * @date 2018/4/11 0011 13:52
 **/
@RestControllerAdvice
public class LmmallExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerResponse processRuntimeException(RuntimeException e) {
        return ServerResponse.createByErrorMessage(e.toString());
    }
}
