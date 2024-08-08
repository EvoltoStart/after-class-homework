package com.wx.controller;

import com.wx.framework.Controller;
import com.wx.framework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloController implements Controller {
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = new ModelAndView("hello");
        modelAndView.addObject("message", "Hello, World!!!");
        return modelAndView;
    }
}
