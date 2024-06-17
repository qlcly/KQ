package com.example.demo.webconfig.securityconfig.loginconfig;

import com.alibaba.fastjson.JSON;
import com.example.demo.webconfig.RequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


@Component
public class LoginFailureAuthenticationHandler implements AuthenticationFailureHandler
{
    private Logger logger = LoggerFactory.getLogger(LoginFailureAuthenticationHandler.class);
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException
    {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = httpServletResponse.getWriter();
//        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        String userName = RequestHolder.username.get();
        Map<String, String> map = new HashMap<>();
        map.put("loginStatus", "fail");
        printWriter.print(JSON.toJSONString(map));
        printWriter.flush();
        printWriter.close();
        logger.warn("Login user: User id: " + userName + ": Failed");
    }
}
