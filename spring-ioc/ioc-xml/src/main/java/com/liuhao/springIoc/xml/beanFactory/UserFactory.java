package com.liuhao.springIoc.xml.beanFactory;

import com.liuhao.springIoc.xml.service.UserService;
import com.liuhao.springIoc.xml.service.impl.User2ServiceImpl;
import org.springframework.beans.factory.FactoryBean;

public class UserFactory implements FactoryBean<UserService> {
    @Override
    public UserService getObject() throws Exception {
        return new User2ServiceImpl();
    }

    @Override
    public Class<?> getObjectType() {
        return User2ServiceImpl.class;
    }
}
