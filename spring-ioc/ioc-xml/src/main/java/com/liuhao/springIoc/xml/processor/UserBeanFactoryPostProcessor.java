package com.liuhao.springIoc.xml.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class UserBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    /**
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        // 可以用此对象修改 bean 定义的一些选项
        BeanDefinition user3Service2 = beanFactory.getBeanDefinition("user3Service2");
        //user3Service2.setLazyInit(true); //修改是否懒加载
        //... 省略其他的设置方式 ...

        // 注册操作
        if(beanFactory instanceof DefaultListableBeanFactory){
            DefaultListableBeanFactory beanFactorySub = (DefaultListableBeanFactory)
                    beanFactory;
            BeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClassName("com.liuhao.springIoc.xml.service.impl.User3ServiceImpl");
            //进行注册操作
            beanFactorySub.registerBeanDefinition("user3Service3",beanDefinition);
        }
    }
}
