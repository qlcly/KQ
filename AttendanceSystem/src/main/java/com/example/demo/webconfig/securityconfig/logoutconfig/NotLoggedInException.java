package com.example.demo.webconfig.securityconfig.logoutconfig;

import org.springframework.security.core.AuthenticationException;


public class NotLoggedInException extends AuthenticationException
{
    public NotLoggedInException(String msg)
    {
        super(msg);
    }

    public NotLoggedInException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
