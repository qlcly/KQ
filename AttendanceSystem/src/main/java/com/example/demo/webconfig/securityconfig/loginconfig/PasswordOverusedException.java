package com.example.demo.webconfig.securityconfig.loginconfig;

import org.springframework.security.core.AuthenticationException;


public class PasswordOverusedException extends AuthenticationException
{
    public PasswordOverusedException(String msg, Throwable t)
    {
        super(msg, t);
    }

    public PasswordOverusedException(String msg)
    {
        super(msg);
    }
}
