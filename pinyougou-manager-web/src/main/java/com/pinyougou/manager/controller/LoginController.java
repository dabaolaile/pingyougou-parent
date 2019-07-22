package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

    @RequestMapping("/name")
    public Map name(){
        //获取当前登录的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap map = new HashMap<>();
        map.put("loginName",name);
        return map;
    }
}
