package com.example.demo.webconfig.securityconfig.authenticationconfig;

import com.example.demo.data.user.UserBo;
import com.example.demo.webservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class UserDetailServiceImpl implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);
    @Override
    public UserDetails loadUserByUsername(String user_id) throws UsernameNotFoundException
    {
        UserBo userBo = new UserBo(userRepository.queryUserById(Long.parseLong(user_id)));
        if (userBo.getPassword() == null)
        {
            logger.warn("Query user: Incorrect user ID");
            throw new UsernameNotFoundException("Incorrect user ID.");
        }
        return userBo;
    }
}
