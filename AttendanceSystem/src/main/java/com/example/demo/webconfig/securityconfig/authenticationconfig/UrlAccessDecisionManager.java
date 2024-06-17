package com.example.demo.webconfig.securityconfig.authenticationconfig;

import com.alibaba.fastjson.JSON;
import com.example.demo.data.approval.Approval;
import com.example.demo.webconfig.RequestHolder;
import com.example.demo.webservice.service.approval.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Component
public class UrlAccessDecisionManager implements AccessDecisionManager
{
    private enum Decision { AND, OR, DYNAMIC }
    private Decision decision;
    private List<UserGrantedAuthority> authorities;
    private List<ConfigAttribute> attributes;
    private Logger logger = LoggerFactory.getLogger(UrlAccessDecisionManager.class);
    private Authentication authentication;
    @Autowired
    private ApprovalService approvalService;
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException
    {
        this.authentication = authentication;
        authorities = (List<UserGrantedAuthority>) authentication.getAuthorities();
        attributes = (List<ConfigAttribute>) collection;
        this.decision = decisionMapper(attributes.get(0).getAttribute());
        boolean accessGranted = initDecision();
        for (int i = 1; i < collection.size(); i += 2)
        {
            if (decision.equals(Decision.AND))
            {
                accessGranted = accessGranted && judge((AuthenticationCompareAttribute) attributes.get(i), (SecurityConfig) attributes.get(i + 1));
                if (!accessGranted)
                    break;
            }
            if (decision.equals(Decision.OR))
            {
                accessGranted = accessGranted || judge((AuthenticationCompareAttribute) attributes.get(i), (SecurityConfig) attributes.get(i + 1));
            }
        }
        if (decision.equals(Decision.DYNAMIC))
        {
            accessGranted = accessGranted && dynamicJudge();
        }
        if (!accessGranted)
        {
            logger.warn("User ID: " + authentication.getPrincipal() + ": Insufficient authority.");
            throw new AccessDeniedException("Insufficient authority.");
        }
    }

    private boolean dynamicJudge()
    {
        HttpServletRequest httpServletRequest = RequestHolder.getHttpServletRequest();
        String method = httpServletRequest.getMethod();
        InputStream inputStream = null;
        String jsonString = "";
        String content = "";
        try
        {
            inputStream = httpServletRequest.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((content = bufferedReader.readLine()) != null)
            {
                jsonString += content;
            }
            if (method.equals("POST"))
            {
                Map<String, Object> map = (Map<String, Object>) JSON.parse(jsonString);
                Approval approval = JSON.parseObject(map.get("Approval").toString(), Approval.class);
                SecurityConfig securityConfig = approvalTypeMapper(approval);
                if (securityConfig == null)
                    return false;
                attributes = Arrays.asList(new DecisionAttribute("AND"), new AuthenticationCompareAttribute("="), securityConfig,
                        new AuthenticationCompareAttribute("<="), new SecurityConfig("GENERAL"));
            }
            else if (method.equals("PUT"))
            {
                Map<String, Object> map = (Map<String, Object>) JSON.parse(jsonString);
                Approval approval = JSON.parseObject(map.get("Approval").toString(), Approval.class);
                String empID = approval.getEmpID().toString();
                if (!authentication.getPrincipal().toString().equals(empID))
                {
                    return false;
                }
                SecurityConfig securityConfig = approvalTypeMapper(approval);
                if (securityConfig == null)
                    return false;
                attributes = Arrays.asList(new DecisionAttribute("AND"), new AuthenticationCompareAttribute("<"), securityConfig,
                        new AuthenticationCompareAttribute("<="), new SecurityConfig("GENERAL"));
            }
            else if (method.equals("DELETE"))
            {
                String empID = httpServletRequest.getParameter("empID");
                if (!authentication.getPrincipal().toString().equals(empID))
                {
                    return false;
                }
                attributes = Arrays.asList(new DecisionAttribute("AND"), new AuthenticationCompareAttribute(">="), new SecurityConfig("DEPARTMENT_MANAGER"),
                        new AuthenticationCompareAttribute("<="), new SecurityConfig("GENERAL"));
            }
            else throw new UnsupportedOperationException("Unsupported http method for dynamic judge");
            boolean accessGranted = true;
            for (int i = 1; i < attributes.size(); i += 2)
            {
                accessGranted = accessGranted && judge((AuthenticationCompareAttribute) attributes.get(i), (SecurityConfig) attributes.get(i + 1));
                if (!accessGranted)
                    break;
            }
            return accessGranted;
        }
        catch (Exception e)
        {
            logger.error("Unable to execute dynamic judge", e);
        }
        return false;
    }

    private boolean judge(AuthenticationCompareAttribute attribute, SecurityConfig requiredAttribute)
    {
        String compareAttribute = attribute.getAttribute();
        String _requiredAttribute = requiredAttribute.getAttribute();
        switch (compareAttribute)
        {
            case "=": return judgeEquals(_requiredAttribute);
            case "<": return judgeLessThan(_requiredAttribute);
            case "<=": return judgeLessEqualsThan(_requiredAttribute);
            case ">": return judgeGreaterThan(_requiredAttribute);
            case ">=": return judgeGreaterEqualsThan(_requiredAttribute);
            default:
            {
                logger.error("Invalid authentication compare attribute: " + attribute);
                throw new IllegalArgumentException("Invalid authentication compare attribute");
            }
        }
    }

    private boolean judgeEquals(String requiredAttribute)
    {
        for (int i = 0; i < authorities.size(); i++)
        {
            try
            {
                if (authorities.get(i).compareTo(new UserGrantedAuthority(requiredAttribute)) == 0)
                {
                    return true;
                }
            }
            catch (RuntimeException e)
            {
            }
        }
        return false;
    }

    private boolean judgeLessThan(String requiredAttribute)
    {
        for (int i = 0; i < authorities.size(); i++)
        {
            try
            {
                if (authorities.get(i).compareTo(new UserGrantedAuthority(requiredAttribute)) < 0)
                {
                    return true;
                }
            }
            catch (RuntimeException e)
            {

            }
        }
        return false;
    }

    private boolean judgeGreaterThan(String requiredAttribute)
    {
        for (int i = 0; i < authorities.size(); i++)
        {
            try
            {
                if (authorities.get(i).compareTo(new UserGrantedAuthority(requiredAttribute)) > 0)
                {
                    return true;
                }
            }
            catch (RuntimeException e)
            {

            }
        }
        return false;
    }

    private boolean judgeLessEqualsThan(String requiredAttribute)
    {
        for (int i = 0; i < authorities.size(); i++)
        {
            try
            {
                if (authorities.get(i).compareTo(new UserGrantedAuthority(requiredAttribute)) <= 0)
                {
                    return true;
                }
            }
            catch (RuntimeException e)
            {

            }
        }
        return false;
    }

    private boolean judgeGreaterEqualsThan(String requiredAttribute)
    {
        for (int i = 0; i < authorities.size(); i++)
        {
            try
            {
                if (authorities.get(i).compareTo(new UserGrantedAuthority(requiredAttribute)) >= 0)
                {
                    return true;
                }
            }
            catch (RuntimeException e)
            {

            }
        }
        return false;
    }

    private boolean initDecision()
    {
        switch (decision)
        {
            case AND: return true;
            case OR: return false;
            case DYNAMIC: return true;
            default:
            {
                logger.error("Invalid decision attribute: " + decision);
                throw new IllegalArgumentException("Invalid decision attribute");
            }
        }
    }

    private Decision decisionMapper(String attribute)
    {
        switch (attribute)
        {
            case "AND": return Decision.AND;
            case "OR": return Decision.OR;
            case "DYNAMIC": return Decision.DYNAMIC;
            default:
            {
                logger.error("Invalid decision attribute: " + decision);
                throw new IllegalArgumentException("Invalid decision attribute");
            }
        }
    }

    private SecurityConfig approvalTypeMapper(int approvalType)
    {
        switch (approvalType)
        {
            case Approval.REFUSE: return null;
            case Approval.UNDETERMINED: return new SecurityConfig("DEPARTMENT_MANAGER");
            case Approval.DEPARTMENT_MANAGER_APPROVAL: return new SecurityConfig("GENERAL_MANAGER");
            case Approval.GENERAL_MANAGER_APPROVAL: return new SecurityConfig("GENERAL_MANAGER");
            default:
            {
                logger.error("Invalid approval type: " + approvalType);
                throw new IllegalArgumentException("Invalid approval type");
            }
        }
    }

    private SecurityConfig approvalTypeMapper(Approval approval)
    {
        List<Approval> approvals = approvalService.getApprovalByID(null, approval.getAppID());
        int approvalType = -1;
        for (Approval approval1: approvals)
        {
            if (approval1.getType() > approvalType)
                approvalType = approval1.getType();
        }
        if (approvalType == -1)
            approvalType = Approval.UNDETERMINED;
        return approvalTypeMapper(approvalType);
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute)
    {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass)
    {
        return true;
    }
}
