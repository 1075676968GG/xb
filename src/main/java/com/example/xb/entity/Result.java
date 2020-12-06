package com.example.xb.entity;

/*** 前后端通用响应实体 */
public class Result {
    // 请求是否成功
    private Boolean flag;
    // 响应给前端的消息
    private String message;
    // 响应给前端的数据
    private Object data;


    public Result() {
    }

    public Result(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public Result(Boolean flag, String message, Object data) {
        this.flag = flag;
        this.message = message;
        this.data = data;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" + "flag=" + flag + ", message='" + message + '\'' + ", data=" + data + '}';
    }
}



