package com.wx.firstweb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class Car extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // 确保接收的请求数据编码为UTF-8
        Map<String, String[]> parameterMap = req.getParameterMap();
        StringBuilder s = new StringBuilder("<table border=\"1\">\n" +
                "        <thead>\n" +
                "        <tr>\n" +
                "            <th>商品名称</th>\n" +
                "            <th>数量</th>\n" +

                "        </tr>\n" +
                "        </thead>\n" +
                "        <tbody>\n");

        // 处理提交的数据，例如更新购物车
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            if (values.length > 0) {
                String num = values[0];
                // 在这里处理每个商品的数量更新逻辑
               s.append("        <tr><td>").append(name).append("</td><td>").append(num).append("</td>");

            }
        }
        s.append("</tbody></table>");

        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println(s);
        out.println("购物车提交成功！");
        out.flush();
    }
}
