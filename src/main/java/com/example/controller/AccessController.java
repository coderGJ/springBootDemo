package com.example.controller;

import com.example.domain.BaseUser;
import com.example.repository.BaseUserRepository;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AccessController extends AbstractController {

    @Autowired
    private BaseUserRepository userRepository;

    @GetMapping(value = "/login")
    public String login() {
        return "access/login";
    }

    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/signUp")
    public String signUp(){
        return "access/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(BaseUser user) {
        log.debug("post signUp");
        String password= new Sha256Hash(user.getPassword(), null , 2).toBase64();
        userRepository.save(user);
        return "access/signUp";
    }
}
