package com.example.demo.webconfig.securityconfig.logoutconfig;

import com.alibaba.fastjson.JSON;
import com.example.demo.data.user.UserBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


@Order(1)
@Component
public class JsonLogoutHandler extends SecurityContextLogoutHandler
{
    @Autowired
    @Qualifier("userDetailServiceImpl")
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(JsonLogoutHandler.class);
    private String userName;
    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
    {
        if(httpServletRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE))
        {
            InputStream inputStream = null;
            String jsonString = "";
            try
            {
                inputStream = httpServletRequest.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String content = "";
                while ((content = bufferedReader.readLine()) != null)
                {
                    jsonString += content;
                }
                Map<String, Object> map =(Map<String, Object>) JSON.parse(jsonString);
                userName = map.get("username").toString();
                String password = map.get("password").toString();
                long userId = Long.parseLong(userName);
                UserBo userBo = (UserBo) userDetailsService.loadUserByUsername(userName);
                if (passwordEncoder.matches(userBo.getPassword(), password))
                {
                    if (UserBo.isLoggedin(userId))
                    {
                        UserBo.logoutUser(userId);
                        logger.info("Logout user: User id: " + userName);
                        super.logout(httpServletRequest, httpServletResponse, authentication);
                    }
                    else
                    {
                        logger.warn("Logout user: User id: " + userName + ": Failed: Not logged in");
                        throw new NotLoggedInException("You are not logged in.");
                    }
                }
                else
                {
                    logger.warn("Logout user: User id: " + userName + ": Failed: Incorrect password");
                    throw new BadCredentialsException("Incorrect password.");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            super.logout(httpServletRequest, httpServletResponse, authentication);
        }
    }
}
