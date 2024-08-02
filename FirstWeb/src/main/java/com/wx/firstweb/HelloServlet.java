package com.wx.firstweb;

import java.io.*;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "list", value = "/list")
public class HelloServlet extends HttpServlet {
    private String message;
    StringBuffer sb=new StringBuffer();
    private ShopCar shopCar=new ShopCar();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,Integer> map=shopCar.getMap();
        sb.delete(0,sb.length());
        sb.append("{\"lists\":[");
        for (Map.Entry<String,Integer> entry:map.entrySet()){
            sb.append("{\"name\":\""+entry.getKey()+"\",\"num\":"+entry.getValue()+"},");
        }
        if (!map.isEmpty()) {
            sb.setLength(sb.length() - 1); // 移除最后一个逗号
        }
        sb.append("]}");
        resp.setContentType("application/json;charset=utf-8");

        PrintWriter out = resp.getWriter();
        out.println(sb.toString());
        out.flush();
    }

}