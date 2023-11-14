package com.liuhao.springioc.annotation.config;

import com.liuhao.springioc.annotation.service.UserService;
import com.liuhao.springioc.annotation.service.impl.User1ServiceImpl;
import org.springframework.context.annotation.Bean;

public class OtherConfig {

    @Bean("user1Service1")
    public UserService newInstance(){
        return new User1ServiceImpl();
    }
}
