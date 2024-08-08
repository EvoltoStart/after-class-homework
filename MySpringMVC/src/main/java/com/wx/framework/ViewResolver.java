package com.wx.framework;

public interface ViewResolver {
    View resolveViewName(String viewName) throws Exception;
}
