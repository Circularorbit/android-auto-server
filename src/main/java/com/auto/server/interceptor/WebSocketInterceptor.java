package com.auto.server.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 从请求中获取查询参数并存入attributes中
        String queryString = request.getURI().getQuery();
        if (queryString != null) {
            String[] queryParams = queryString.split("&");
            for (String queryParam : queryParams) {
                String[] keyValue = queryParam.split("=");
                if (keyValue.length == 2) {
                    attributes.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) {
        // 握手完成后不需要执行任何操作
    }
}