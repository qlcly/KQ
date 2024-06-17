package com.example.demo.webconfig;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Component
public class HttpServletRequestWrapperFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper((HttpServletRequest) request);
            chain.doFilter(requestWrapper, response);
        }
        else
            {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy()
    {

    }
}
