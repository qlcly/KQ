package com.example.demo.webconfig.securityconfig.authenticationconfig;

import com.example.demo.data.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class UserGrantedAuthority implements GrantedAuthority, Comparable<UserGrantedAuthority>
{
    private Logger logger = LoggerFactory.getLogger(UserGrantedAuthority.class);
    private static final Set<String> availableUserAuthorities = new HashSet<String>()
    {
        {
            addAll(Arrays.asList(new String[]{"ADMINISTRATOR", "PERSONNEL", "GENERAL", "STAFF", "DEPARTMENT_MANAGER", "GENERAL_MANAGER"}));
        }
    };
    private String authority;
    private int authorityNum;
    public UserGrantedAuthority(int authority)
    {
        setAuthority(authority);
    }
    public UserGrantedAuthority(String authority)
    {
        if (availableUserAuthorities.contains(authority))
        {
            this.authority = authority;
            this.authorityNum = authorityMapper(authority);
        }
    }
    @Override
    public String getAuthority()
    {
        return this.authority;
    }

    private void setAuthority(int authority)
    {
        this.authorityNum = authority;
        this.authority = authorityMapper(authority);
    }

    private String authorityMapper(int authority)
    {
        switch (authority)
        {
            case User.ADMINISTRATOR: return "ADMINISTRATOR";
            case User.PERSONNEL: return "PERSONNEL";
            case User.GENERAL: return "GENERAL";
            case User.STAFF: return "STAFF";
            case User.DEPARTMENT_MANAGER: return "DEPARTMENT_MANAGER";
            case User.GENERAL_MANAGER: return "GENERAL_MANAGER";
            default:
            {
                logger.error("Invalid authority: " + authority);
                throw new IllegalArgumentException("Invalid authority: " + authority);
            }
        }
    }

    private int authorityMapper(String authority)
    {
        switch (authority)
        {
            case "ADMINISTRATOR": return User.ADMINISTRATOR;
            case "PERSONNEL": return User.PERSONNEL;
            case "GENERAL": return User.GENERAL;
            case "STAFF": return User.STAFF;
            case "DEPARTMENT_MANAGER": return User.DEPARTMENT_MANAGER;
            case "GENERAL_MANAGER": return User.GENERAL_MANAGER;
            default:
            {
                logger.error("Invalid authority: " + authority);
                throw new IllegalArgumentException("Invalid authority: " + authority);
            }
        }
    }

    @Override
    public int compareTo(UserGrantedAuthority o)
    {
        if (this.authorityNum >= User.STAFF && this.authorityNum <= User.GENERAL_MANAGER)
        {
            if (o.authorityNum >= User.STAFF && o.authorityNum <= User.GENERAL_MANAGER)
            {
                return Integer.compare(this.authorityNum, o.authorityNum);
            }
            else
            {
                throw new IllegalArgumentException("Unable to compare");
            }
        }
        if (this.authorityNum >= User.ADMINISTRATOR && this.authorityNum <= User.GENERAL)
        {
            if (o.authorityNum >= User.ADMINISTRATOR && o.authorityNum <= User.GENERAL)
            {
                return Integer.compare(this.authorityNum, o.authorityNum);
            }
            else
            {
                throw new IllegalArgumentException("Unable to compare");
            }
        }
        throw new IllegalArgumentException("Unable to compare");
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserGrantedAuthority that = (UserGrantedAuthority) o;
        return authorityNum == that.authorityNum;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(authorityNum);
    }
}
