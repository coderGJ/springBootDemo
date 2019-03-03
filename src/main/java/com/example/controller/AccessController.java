package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccessController extends AbstractController{

    @RequestMapping(value = "/login")
    public String login(String username, String password) {
        if (!StringUtils.isEmpty(username)) {

        }
        return "access/login";
    }

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @RequestMapping("/signUp")
    public String signUp(){
        return "access/signUp";
    }
}
