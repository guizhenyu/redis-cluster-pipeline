package com.redis.cluster.common;

import com.redis.cluster.common.enums.MessageCode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: Response
 * date: 2019/11/20 10:43 上午
 * author: guizhenyu
 */
public class Response<T> {

    private MessageCode status;

    private List<String> messages;

    private T result;

    public Response() {
        messages = new ArrayList<>();
    }

    public Response(MessageCode status, T result) {
        messages = new ArrayList<>();
        this.status = status;
        this.result = result;
    }

    public MessageCode getStatus() {
        return status;
    }

    public void setStatus(MessageCode status) {
        this.status = status;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "error code:" + status + " result:" + result;
    }

    public static Response failure(String msg) {
        Response resp = new Response();
        resp.status = MessageCode.COMMON_FAILURE;
        resp.getMessages().add(msg);
        return resp;
    }

    public static Response failure(MessageCode messageCode) {
        Response resp = new Response();
        resp.status = messageCode;
        resp.getMessages().add(messageCode.getMsg());
        return resp;
    }

    public static Response failure(String code, String message) {
        Response resp = new Response();
        MessageCode messageCode = null;
        for (MessageCode temp : MessageCode.values()) {
            if (temp.getCode().equals(code)) {
                messageCode = temp;
                break;
            }
        }
        if (null == messageCode) {
            messageCode = MessageCode.COMMON_FAILURE;
        }
        resp.status = messageCode;
        resp.getMessages().add(message);
        return resp;
    }

    public static Response failure(MessageCode messageCode, String message) {
        Response resp = new Response();
        resp.status = messageCode;
        resp.getMessages().add(messageCode.getMsg());
        if (StringUtils.isNotBlank(message)) {
            resp.getMessages().add(message);
        }
        return resp;
    }


    public static Response success() {
        Response resp = new Response();
        resp.status = MessageCode.COMMON_SUCCESS;
        resp.getMessages().add(MessageCode.COMMON_SUCCESS.getMsg());
        return resp;
    }

    public static <K> Response<K> success(K t) {
        Response<K> resp = new Response<>();
        resp.status = MessageCode.COMMON_SUCCESS;
        resp.getMessages().add(MessageCode.COMMON_SUCCESS.getMsg());
        resp.result = t;

        return resp;
    }
}
