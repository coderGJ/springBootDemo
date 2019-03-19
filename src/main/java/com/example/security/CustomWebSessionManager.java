package com.example.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class CustomWebSessionManager extends DefaultWebSessionManager {

    private static final Logger log = LoggerFactory.getLogger(CustomWebSessionManager.class);

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

        Serializable sessionId = this.getSessionId(sessionKey);
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey && sessionId != null) {

            request = ((WebSessionKey) sessionKey).getServletRequest();
            Session session = (Session) request.getAttribute(sessionId.toString());
            if (session != null) {
                log.trace("retrieveSession by request");
                return session;
            }
        }
        Session session = super.retrieveSession(sessionKey);

        if (request != null && sessionId != null) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = (String) super.getSessionId(request, response);
        if (StringUtils.isBlank(id)) {
            HttpServletRequest httpRequest = WebUtils.toHttp(request);
            id = httpRequest.getHeader(getSessionIdCookie().getName());
        }
        return id;
    }
}