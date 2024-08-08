package com.wx.framework;

public class ServiceViewResolver implements ViewResolver {
    @Override
    public View resolveViewName(String viewName) throws Exception {
        return new HtmlView("/WEB-INF/views/" + viewName + ".jsp");
    }
}