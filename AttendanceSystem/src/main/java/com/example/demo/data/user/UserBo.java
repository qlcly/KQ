package com.example.demo.data.user;

import com.example.demo.webconfig.securityconfig.authenticationconfig.UserGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户类，用于登录以及权限验证。
 * @author 林瑞健
 * @since 1.0
 * @version 1.0.1
 * 删除无用属性
 * @version 1.1
 * 添加检测密码滥用功能
 * @version 1.1.1
 * 修复无法正常重复登录
 * @see User
 * @see UserDetails
 */
public class UserBo extends User implements UserDetails
{
    private String password;
    private Logger logger = LoggerFactory.getLogger(UserBo.class);
    private static final Map<Long, Key> loginUserIdSet = new ConcurrentHashMap<>();
    private static final Map<Long, List<String>> usedPassword = new ConcurrentHashMap<>();
    public UserBo(UserDo userDo)
    {
        super();
        this.password = userDo.getPassword();
        setAuth(userDo.getAuth());
        setId(userDo.getId());
        setName(userDo.getName());
        setType(userDo.getType());
    }

    public void setPassword(String password)
    {
        if (password.matches("^([0-9]|[a-z]|[A-Z]){6,30}$"))
        {
            this.password = password;
        }
        else
        {
            logger.warn("Invalid user password");
            throw new IllegalArgumentException("Invalid user password");
        }
    }

    /**
     * 登录用户，记录用户的token密钥和使用过的密文密码
     * @since 1.0
     * @param userId 用户名
     * @param tokenKey token密钥
     * @param password 密码（使用BCrypt加密）
     */
    public static void loginUser(long userId, Key tokenKey, String password)
    {
        List<String> passwords = null;
        if (!usedPassword.containsKey(userId))
        {
            passwords = new ArrayList<>();

        }
        else
        {
            passwords = usedPassword.get(userId);
        }
        passwords.add(password);
        usedPassword.put(userId, passwords);
        loginUserIdSet.put(userId, tokenKey);
    }

    /**
     * 检测密码是否被滥用
     * @since 1.1
     * @param userId 用户ID
     * @param password 用户密码（使用BCrypt加密）
     * @return true：密码被多次使用
     */
    public static boolean isPasswordOverused(long userId, String password)
    {
        if (usedPassword.containsKey(userId))
        {
            return usedPassword.get(userId).contains(password);
        }
        return false;
    }

    public static boolean isLoggedin(long userId)
    {
        return loginUserIdSet.containsKey(userId);
    }

    public static void logoutUser(long userId)
    {
        loginUserIdSet.remove(userId);
    }

    public static Key getUserTokenKey(long userId)
    {
        return loginUserIdSet.get(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        List<UserGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new UserGrantedAuthority(getAuth()));
        authorities.add(new UserGrantedAuthority(getType()));
        return authorities;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.getName();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

}
