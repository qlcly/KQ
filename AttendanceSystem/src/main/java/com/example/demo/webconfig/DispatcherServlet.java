package com.example.demo.webconfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/**")
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet
{
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // 全局替换request对象
        super.service(new BodyReaderHttpServletRequestWrapper(request), response);
    }
}