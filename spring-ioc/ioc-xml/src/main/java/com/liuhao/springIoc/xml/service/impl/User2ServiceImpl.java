package com.liuhao.springIoc.xml.service.impl;


import com.liuhao.springIoc.xml.service.UserService;

public class User2ServiceImpl implements UserService {
    @Override
    public void show() {
        System.out.println("User2ServiceImpl 执行了初始化方法");
    }
}
