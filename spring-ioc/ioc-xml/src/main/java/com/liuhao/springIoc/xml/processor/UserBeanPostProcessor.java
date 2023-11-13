package com.liuhao.springIoc.xml.processor;

import com.liuhao.springIoc.xml.service.impl.User3ServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class UserBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization:" + beanName + ": 执行了");
        if (beanName.equals("user3Service2")){
            User3ServiceImpl user3Service = (User3ServiceImpl)bean;
            user3Service.setName("盖伦");
            System.out.println("将 user3Service2 的 name 改成了 盖伦");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessAfterInitialization:" + beanName + ": 执行了");
        return bean;
    }
}
