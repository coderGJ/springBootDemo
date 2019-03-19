package com.example.security;

import com.example.domain.BaseUser;
import com.example.repository.BaseUserRepository;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomRealm extends AuthorizingRealm {
    private final Logger log = LoggerFactory.getLogger(CustomRealm.class);

    @Autowired
    private BaseUserRepository userRepository;

    public CustomRealm() {
        setName("CustomRealm");
    }

    /**
     * validate the login method of user name and password
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        BaseUser user = userRepository.getByUsername(username);

        if (user == null) {
            throw new UnknownAccountException("not lookup " + username);
        }
        log.debug("doGetAuthenticationInfo username: {}", username);
        return new SimpleAuthenticationInfo(username, user.getPassword(), getName());
    }

    /**
     * get the permission add into the simpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.fromRealm(getName()).iterator().next();
        if (username != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("ADMIN");
            return info;
        } else {
            return null;
        }
    }


}
