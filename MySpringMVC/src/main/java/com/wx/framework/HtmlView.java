package com.wx.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HtmlView implements View {
    private String url;

    public HtmlView(String url) {
        this.url = url;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                request.getServletContext().getResourceAsStream(url), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (Map.Entry<String, ?> entry : model.entrySet()) {
                    line = line.replace("${" + entry.getKey() + "}", entry.getValue().toString());
                }
                writer.println(line);
            }
        }
    }
}