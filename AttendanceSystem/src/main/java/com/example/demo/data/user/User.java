package com.example.demo.data.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户类
 * @author 林瑞健
 * @since 1.0
 * @see UserBo
 * @see UserDo
 * @see UserVo
 * @version 1.1
 * 删除无用属性和权限
 */
public class User
{
    private long id;
    private String name;
    private int auth;
    private int type;
    protected Logger logger = LoggerFactory.getLogger(User.class);
    private static final Set<Integer> availableAuth = new HashSet<Integer>()
    {
        {
            addAll(Arrays.asList(STAFF, DEPARTMENT_MANAGER, GENERAL_MANAGER));
        }
    };
    private static final Set<Integer> availableType = new HashSet<Integer>()
    {
        {
            addAll(Arrays.asList(GENERAL, PERSONNEL, ADMINISTRATOR));
        }
    };
    public static final int STAFF = 0;
    public static final int DEPARTMENT_MANAGER = 1;
    public static final int GENERAL_MANAGER = 2;
    public static final int GENERAL = -1;
    public static final int PERSONNEL = -2;
    public static final int ADMINISTRATOR = -4;
    protected User()
    {
    }

    public int getAuth()
    {
        return auth;
    }

    public int getType()
    {
        return type;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setAuth(int auth)
    {
        if (availableAuth.contains(auth))
        {
            this.auth = auth;
        }
        else
        {
            logger.warn("Invalid user auth: " + auth);
            throw new IllegalArgumentException("Invalid user auth");
        }
    }

    public void setType(int type)
    {
        if (availableType.contains(type))
        {
            this.type = type;
        }
        else
        {
            logger.warn("Invalid user type: " + type);
            throw new IllegalArgumentException("Invalid user type");
        }
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        if (name.matches("^([a-z]|[A-Z]|[\\u4e00-\\u9fa5])+$"))
        {
            this.name = name;
        }
        else
        {
            logger.warn("Invalid user name: " + name);
            throw new IllegalArgumentException("Invalid user name");
        }
    }
}
