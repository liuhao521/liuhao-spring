package com.liuhao.springioc.annotation.test;

import com.liuhao.springioc.annotation.config.MainConfig;
import com.liuhao.springioc.annotation.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test  {


    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        UserService bean = (UserService)applicationContext.getBean("user1ServiceImpl");
        UserService bean1 = (UserService)applicationContext.getBean("user1Service1");
        System.out.println(bean);
        System.out.println(bean1);
    }
}
