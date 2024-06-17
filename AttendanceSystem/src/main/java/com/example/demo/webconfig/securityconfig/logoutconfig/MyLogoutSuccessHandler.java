package com.example.demo.webconfig.securityconfig.logoutconfig;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Order(2)
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler
{
    private String userName;
    private Logger logger = LoggerFactory.getLogger(MyLogoutSuccessHandler.class);
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException
    {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter printWriter = httpServletResponse.getWriter();
        Map<String, String> map = new HashMap<>();
        map.put("logoutStatus", "success");
        printWriter.print(JSON.toJSONString(map));
        printWriter.flush();
        printWriter.close();
        logger.info("Logout user: Successful");
    }
}
