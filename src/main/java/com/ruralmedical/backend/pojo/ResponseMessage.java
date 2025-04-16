package com.ruralmedical.backend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ResponseMessage<T> success(T data) {
        return new ResponseMessage<>(HttpStatus.OK.value(), "success", data);
    }
    public static <T> ResponseMessage<T> success(String message,T data) {
        return new ResponseMessage<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseMessage<T> fail(int code, String message) {
        return new ResponseMessage<>(code, message, null);
    }

}
