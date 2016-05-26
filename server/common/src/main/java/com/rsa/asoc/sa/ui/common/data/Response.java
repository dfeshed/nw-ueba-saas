package com.rsa.asoc.sa.ui.common.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic wrapper class that describes the data being returned from a WebSocket request.  It
 * supports {@link ResponseCode}s and returns the {@link Request}.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 * @param <T> the type of data to be returned
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private ResponseCode code = ResponseCode.SUCCESS;
    private T data;
    private Request request;
    private Map<String, Object> meta;

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public Response(T data, Request request) {
        this.data = data;
        this.request = request;
    }

    public Response(T data, Long total, Request request) {
        this(data, request);
        setTotal(total);
    }

    public Response(T data, Map<String, Object> meta, Request request) {
        this(data, request);
        setMeta(meta);
    }

    public Response(ResponseCode code, Request request) {
        this.code = code;
        this.request = request;
    }

    public ResponseCode getCode() {
        return code;
    }

    public Response<T> setCode(ResponseCode code) {
        this.code = code;
        return this;
    }

    public T getData() {
        return data;
    }

    public Response<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public Response<T> setRequest(Request request) {
        this.request = request;
        return this;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public Response<T> setMeta(Map<String, Object> meta) {
        this.meta = meta;
        return this;
    }

    public Response<T> addMeta(String key, Object value) {
        if (meta == null) {
            meta = new HashMap<>();
        }
        meta.put(key, value);
        return this;
    }

    public Response<T> setTotal(Long total) {
        return addMeta("total", total);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("code", code)
                .add("data", data)
                .add("request", request)
                .add("meta", meta)
                .toString();
    }
}
