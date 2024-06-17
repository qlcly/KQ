package com.example.demo.webconfig.securityconfig.loginconfig;

import org.springframework.security.core.AuthenticationException;


public class AlreadyLoggedInException extends AuthenticationException
{
    public AlreadyLoggedInException(String msg, Throwable t)
    {
        super(msg, t);
    }

    public AlreadyLoggedInException(String msg)
    {
        super(msg);
    }
}
