package com.su.Web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//or add RestController
@Controller
public class HomeControler {
    

    @RequestMapping("/")
    @ResponseBody
    public String greet(){
        return "welcom";
    }

    @RequestMapping("/about")
    @ResponseBody
    public String about(){
        return "its about";
    }




}
