package com.example.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomRealm extends AuthorizingRealm {
    private Logger log = LoggerFactory.getLogger(CustomRealm.class);

    public CustomRealm() {
        setName("AuthorizeProvider");
        setCredentialsMatcher(new HashedCredentialsMatcher(Sha256Hash.ALGORITHM_NAME));
    }

    /**
     * validate the login method of user name and password
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        String pwd = new Sha256Hash(new String(token.getPassword())).toHex();
        String username = token.getUsername();

        return new SimpleAuthenticationInfo(username, pwd, getName());
    }

    /**
     * get the permission add into the simpleAuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
        PrincipalCollection principals) {
        String username = (String) principals.fromRealm(getName()).iterator()
            .next();
        if (username != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            info.addRole("ADMIN");
            return info;
        } else {
            return null;
        }
    }


}
