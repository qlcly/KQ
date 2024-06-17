package com.example.demo.webconfig.securityconfig.authenticationconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


@Component
public class UrlPathFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource
{
    private Map<RequestMatcher, Collection<ConfigAttribute>> urlAuthorityMap = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(UrlPathFilterInvocationSecurityMetadataSource.class);
    public UrlPathFilterInvocationSecurityMetadataSource()
    {
        File file = null; //配置文件格式：[网址] [HTTP方法] [AND|OR] ([>|>=|=|<|<=] [权限])+
        BufferedReader bufferedReader = null;
        try
        {
            file = new ClassPathResource("urlAuthority.settings").getFile();
            bufferedReader = new BufferedReader(new FileReader(file));
            String content;
            while ((content = bufferedReader.readLine()) != null)
            {
                String[] setting = content.split("[ ]+");
                if (setting.length <= 1)
                    continue;

                if (setting[0].startsWith("#"))
                    continue;
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(setting[0], setting[1]);
                List<ConfigAttribute> configs = new ArrayList<>();
                configs.add(new DecisionAttribute(setting[2]));
                for (int i = 3; i < setting.length; i += 2)
                {
                    if (setting[i].startsWith("//"))
                        continue;
                    configs.add(new AuthenticationCompareAttribute(setting[i]));
                    configs.add(new SecurityConfig(setting[i + 1]));
                }
                urlAuthorityMap.put(matcher, configs);
            }
            logger.info("Read URL Authorities successful");
        }
        catch (Exception e)
        {
            logger.error("Unable to read URL Authorities", e);
            System.exit(1);
        }
        finally
        {
            if (bufferedReader != null)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException ignored)
                {

                }
            }
        }

    }
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException
    {
        FilterInvocation filterInvocation = (FilterInvocation) o;
        HttpServletRequest request = filterInvocation.getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry: urlAuthorityMap.entrySet())
        {
            if (entry.getKey().matches(request))
            {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes()
    {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }
}
