package com.example.demo.webconfig.securityconfig.loginconfig;

import com.alibaba.fastjson.JSON;
import com.example.demo.webconfig.RequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class JsonLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private Logger logger = LoggerFactory.getLogger(JsonLoginAuthenticationFilter.class);
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        if(request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE))
        {
            UsernamePasswordAuthenticationToken authRequest = null;
            InputStream inputStream = null;
            String jsonString = "";
            try
            {
                inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String content = "";
                while ((content = bufferedReader.readLine()) != null)
                {
                    jsonString += content;
                }
                Map<String, Object> map =(Map<String, Object>) JSON.parse(jsonString);
                authRequest = new UsernamePasswordAuthenticationToken(map.get("username").toString(), map.get("password").toString());
                RequestHolder.password.set(map.get("password").toString());
                RequestHolder.username.set(map.get("username").toString());
            }
            catch (Exception e)
            {
                logger.error("Unable to read user info in json format: " + e);
                authRequest = new UsernamePasswordAuthenticationToken("", "");
            }
            finally
            {
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            }
        }
        else
        {
            return super.attemptAuthentication(request, response);
        }
    }
}
