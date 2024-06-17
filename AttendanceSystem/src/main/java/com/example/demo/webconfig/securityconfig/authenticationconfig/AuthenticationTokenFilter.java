package com.example.demo.webconfig.securityconfig.authenticationconfig;

import com.example.demo.data.user.UserBo;
import com.example.demo.webconfig.RequestHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter
{
    @Autowired
    @Qualifier("userDetailServiceImpl")
    private UserDetailsService userDetailsService;
    private Logger logger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        String token = httpServletRequest.getHeader("Token");
        if (token != null)
        {
            String userName = httpServletRequest.getHeader("username");
            long userId = Long.parseLong(userName);
            Claims claims = null;
            try
            {
                claims = (Claims) Jwts.parserBuilder().setSigningKey(UserBo.getUserTokenKey(userId)).build().parse(token).getBody();
            }
            catch (JwtException e)
            {
                logger.warn("User ID: " + userId + ": Invalid token: " + e.getMessage());
                filterChain.doFilter(httpServletRequest, httpServletResponse);
            }
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null && userId == Long.parseLong(userName))
            {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if (userDetails != null)
                {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    //设置登录信息
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    RequestHolder.username.set(userName);
                    logger.info("User ID: " + userId + ": token accepted");
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}