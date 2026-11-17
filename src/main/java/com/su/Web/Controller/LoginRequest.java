package com.su.Web.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginRequest {
    
    @RequestMapping("/login")
    public String login(){
        return "Login page";
    }
}
