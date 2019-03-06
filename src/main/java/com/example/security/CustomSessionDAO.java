package com.example.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import java.util.List;

/**
 * @author GuoJun
 * @since 1.0.0
 * Created by GuoJun on 2018/12/11
 */
public class CustomSessionDAO extends EnterpriseCacheSessionDAO {

    private static final Logger log = LoggerFactory.getLogger(CustomSessionDAO.class);

    @Override
    protected void doDelete(Session session) {
        //删除session
        log.debug("do delete session id: {}", session.getId());
    }

    @Override
    protected void doUpdate(Session session) throws UnknownSessionException {
        //当是ValidatingSession 无效的情况下
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            //序列化Session
            log.debug("session isValid");
            return;
        }

        //用户名
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        log.debug("username {} do update", username);
    }

    @Override
    protected Serializable doCreate(Session session) {
        //生成session的id
        Serializable sessionId = generateSessionId(session);
        //给Session设定id
        assignSessionId(session, sessionId);

        //用户名
         String username = (String) SecurityUtils.getSubject().getPrincipal();

        log.debug("create session,username {}, sessionId is {}", username, sessionId.toString());
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.debug("retrieveSession by mySql");
        //获取session的字符串


        return null;
    }

    /**
     * 通过名称来获取用户 Session
     *
     * @param username
     * @return
     */
    public List<Session> loadByUserName(String username) {
        //获取session的字符串

        return null;
    }

    @Override
    public Collection<Session> getActiveSessions() {

        List<Session> list = new ArrayList<>();
        List<Session> result = new ArrayList<>();
        for (Session session : list) {
            //加载session数据
        }

        return result;
    }
}