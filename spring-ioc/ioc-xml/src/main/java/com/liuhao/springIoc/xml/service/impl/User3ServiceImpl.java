package com.liuhao.springIoc.xml.service.impl;


import com.liuhao.springIoc.xml.service.OrderService;
import com.liuhao.springIoc.xml.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class User3ServiceImpl implements UserService {

    private String name;

    private Integer age;

    private OrderService orderService;

    private List<String> list;

    private Map<String,String> map;

    private List<OrderService> orderServices;

    private Properties properties;


    public User3ServiceImpl() {
    }

    public User3ServiceImpl(String name, Integer age, OrderService orderService) {
        this.name = name;
        this.age = age;
        this.orderService = orderService;
    }

    @Override
    public void show() {
        System.out.println("User3ServiceImpl 执行了初始化方法");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public List<OrderService> getOrderServices() {
        return orderServices;
    }

    public void setOrderServices(List<OrderService> orderServices) {
        this.orderServices = orderServices;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
