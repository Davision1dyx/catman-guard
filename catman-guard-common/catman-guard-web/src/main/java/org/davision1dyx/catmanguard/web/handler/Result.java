package org.davision1dyx.catmanguard.web.handler;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private String code;
    private T data;
    private String message;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setData(data);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode("200");
        result.setData(data);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(String code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(null);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(int code, String message) {
        return fail(String.valueOf(code), message);
    }

    public static <T> Result<T> fail(String message) {
        return fail("500", message);
    }
}