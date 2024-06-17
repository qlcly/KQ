package com.example.demo.webconfig.securityconfig.loginconfig;

import com.alibaba.fastjson.JSON;
import com.example.demo.data.user.UserBo;
import com.example.demo.webconfig.RequestHolder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class LoginSuccessAuthenticationHandler implements AuthenticationSuccessHandler
{
    private Logger logger = LoggerFactory.getLogger(LoginSuccessAuthenticationHandler.class);
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long expiration = 2 * 60 * 60; //2 hours
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException
    {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        String password = RequestHolder.password.get();
        PrintWriter printWriter = httpServletResponse.getWriter();
        String userName = authentication.getPrincipal().toString();
        String token = Jwts.builder().setSubject(userName).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)).signWith(key).compact();
        Map<String, String> map = new HashMap<>();
        map.put("loginStatus", "success");
        map.put("Token", token);
        printWriter.print(JSON.toJSONString(map));
        printWriter.flush();
        printWriter.close();
        UserBo.loginUser(Long.parseLong(userName), key, password);
        logger.info("Login user: User id:" + userName + ": Successful");
    }
}
