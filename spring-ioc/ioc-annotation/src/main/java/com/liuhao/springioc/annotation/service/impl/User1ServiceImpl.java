package com.liuhao.springioc.annotation.service.impl;


import com.liuhao.springioc.annotation.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class User1ServiceImpl implements UserService {

    @Override
    @PostConstruct
    public void show() {
        System.out.println("User1ServiceImpl 执行了初始化方法");
    }
}
