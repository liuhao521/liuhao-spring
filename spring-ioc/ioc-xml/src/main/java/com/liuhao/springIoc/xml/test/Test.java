package com.liuhao.springIoc.xml.test;

import com.liuhao.springIoc.xml.service.UserService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        UserService user1Service1 = (UserService)applicationContext.getBean("user1Service");
        System.out.println(user1Service1);

        UserService user2Service1 = (UserService)applicationContext.getBean("user2Service1");
        System.out.println(user2Service1);

        UserService user2Service2 = (UserService)applicationContext.getBean("user2Service2");
        System.out.println(user2Service2);

    }
}
