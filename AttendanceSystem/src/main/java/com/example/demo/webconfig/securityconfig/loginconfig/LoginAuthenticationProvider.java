package com.example.demo.webconfig.securityconfig.loginconfig;

import com.example.demo.data.user.UserBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class LoginAuthenticationProvider implements AuthenticationProvider
{
    @Qualifier("userDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private Logger logger = LoggerFactory.getLogger(LoginAuthenticationProvider.class);
    private String userId;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        userId = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        String correctPassword = userDetails.getPassword();
        if (passwordEncoder.matches(correctPassword, password))
        {
            if (UserBo.isPasswordOverused(Long.parseLong(userId), password))
            {
                logger.warn("Login user: User id: " + userId + ": Password Overused");
                throw new PasswordOverusedException("Your password is overused");
            }
//            if (UserBo.isLoggedin(Long.parseLong(userId)))
//            {
//                logger.warn("Login user: User id: " + userId + ": Already logged in");
//                throw new AlreadyLoggedInException("You have already logged in.");
//            }
            else
            {
                return new UsernamePasswordAuthenticationToken(userId, password, userDetails.getAuthorities());
            }
        }
        else
        {
            logger.warn("Login user: User id: " + userId + ": Incorrect password");
            throw new BadCredentialsException("Incorrect password.");
        }
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return true;
    }
}
