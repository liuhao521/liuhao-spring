package com.liuhao.springIoc.xml.test;

import com.liuhao.springIoc.xml.service.UserService;
import com.liuhao.springIoc.xml.service.impl.User3ServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FieldDiTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("field-di-beans.xml");

        UserService user3Service1 = (UserService)applicationContext.getBean("user3Service1");
        System.out.println("有参构造注入：" + user3Service1);

        User3ServiceImpl user3Service2 = (User3ServiceImpl)applicationContext.getBean("user3Service2");
        System.out.println("set注入：" + user3Service2);
        System.out.println("user3Service2 的 name：" + user3Service2.getName());

        User3ServiceImpl user3Service3 = (User3ServiceImpl)applicationContext.getBean("user3Service3");
        System.out.println("beanFactoryPostProcessor：" + user3Service3);

        User3ServiceImpl user3Service4 = (User3ServiceImpl)applicationContext.getBean("user3Service4");
        System.out.println("专门用于注册的 beanFactoryPostProcessor：" + user3Service4);
    }
}
