package com.wx.framework;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {
    private HandlerMapping handlerMapping;
    private HandlerAdapter handlerAdapter;
    private ViewResolver viewResolver;

    @Override
    public void init() throws ServletException {
        handlerMapping = new ServiceHandlerMapping();
        handlerAdapter = new ServiceHandlerAdapter();
        viewResolver = new ServiceViewResolver();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object handler = handlerMapping.getHandler(req);
        if (handler == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            ModelAndView modelAndView = handlerAdapter.handle(req, resp, handler);
            View view = viewResolver.resolveViewName(modelAndView.getViewName());
            view.render(modelAndView.getModel(), req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
