package com.example.demo.webconfig;

import com.example.demo.webconfig.securityconfig.authenticationconfig.AuthenticationTokenFilter;
import com.example.demo.webconfig.securityconfig.loginconfig.JsonLoginAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Qualifier("userDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    @Qualifier("loginSuccessAuthenticationHandler")
    private AuthenticationSuccessHandler successAuthenticationHandler;
    @Autowired
    @Qualifier("loginFailureAuthenticationHandler")
    private AuthenticationFailureHandler failureAuthenticationHandler;
    @Autowired
    private FilterInvocationSecurityMetadataSource urlPathFilterInvocationSecurityMetadataSource;
    @Autowired
    private AccessDecisionManager decisionManager;
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private AuthenticationTokenFilter authenticationTokenFilter;
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private CorsFilter corsFilter;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RequestHolder requestHolder;
    @Autowired
    private HttpServletRequestWrapperFilter httpServletRequestWrapperFilter;
    private Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public JsonLoginAuthenticationFilter jsonAuthenticationFilter() throws Exception
    {
        JsonLoginAuthenticationFilter jsonLoginAuthenticationFilter = new JsonLoginAuthenticationFilter();
        jsonLoginAuthenticationFilter.setAuthenticationSuccessHandler(successAuthenticationHandler);
        jsonLoginAuthenticationFilter.setAuthenticationFailureHandler(failureAuthenticationHandler);
        jsonLoginAuthenticationFilter.setFilterProcessesUrl("/login");
        jsonLoginAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return jsonLoginAuthenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.authenticationProvider(authenticationProvider);
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.cors().configurationSource(corsConfigurationSource());
        http.csrf().disable().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.addFilterBefore(httpServletRequestWrapperFilter, WebAsyncManagerIntegrationFilter.class);
        http.addFilterAt(requestHolder, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(requestHolder, LogoutFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login", "/logout", "/xml/**").permitAll();
//        http.authorizeRequests().anyRequest().permitAll();
        http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>()
        {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o)
            {
                o.setAccessDecisionManager(decisionManager);
                o.setSecurityMetadataSource(urlPathFilterInvocationSecurityMetadataSource);
                return o;
            }
        }).anyRequest().authenticated();
        http.formLogin()
                .successHandler(successAuthenticationHandler)
                .failureHandler(failureAuthenticationHandler).permitAll().and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        http.addFilterAt(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.logout().addLogoutHandler(logoutHandler).logoutSuccessHandler(logoutSuccessHandler).permitAll();
        http.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        logger.info("Read web security config successful");
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
