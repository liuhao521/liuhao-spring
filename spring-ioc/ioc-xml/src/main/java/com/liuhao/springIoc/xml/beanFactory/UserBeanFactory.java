package com.liuhao.springIoc.xml.beanFactory;

import com.liuhao.springIoc.xml.service.UserService;
import com.liuhao.springIoc.xml.service.impl.User2ServiceImpl;

public class UserBeanFactory {

    public static UserService createUser2Service(){
        return new User2ServiceImpl();
    }

    public UserService getUser2Service(){
        return new User2ServiceImpl();
    }
}
