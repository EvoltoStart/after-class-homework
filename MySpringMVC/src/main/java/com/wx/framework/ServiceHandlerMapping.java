package com.wx.framework;

import com.wx.controller.HelloController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class ServiceHandlerMapping implements HandlerMapping {
    private Map<String, Object> handlerMap;

    public ServiceHandlerMapping() {
        handlerMap = new HashMap<>();
        handlerMap.put("/hello", new HelloController());
    }

    @Override
    public Object getHandler(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return handlerMap.get(uri);
    }
}