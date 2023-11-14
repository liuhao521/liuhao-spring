package com.liuhao.springIoc.xml.test;

import com.liuhao.springIoc.xml.service.UserService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InstanceTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");

        // 无参构造实例化
        UserService user1Service1 = (UserService)applicationContext.getBean("user1Service");
        System.out.println("无参构造实例化:" + user1Service1);

        // 静态工厂实例化
        UserService user2Service1 = (UserService)applicationContext.getBean("user2Service1");
        System.out.println("静态工厂实例化:" + user2Service1);

        // 实例工厂创建
        UserService user2Service2 = (UserService)applicationContext.getBean("user2Service2");
        System.out.println("实例工厂创建:" + user2Service2);

        // 实例工厂创建
        UserService user2Service3 = (UserService)applicationContext.getBean("user2Service3");
        System.out.println("实现FactoryBean:" + user2Service3);

    }
}
