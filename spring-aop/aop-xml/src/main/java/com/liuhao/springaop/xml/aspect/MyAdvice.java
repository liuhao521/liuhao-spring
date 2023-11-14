package com.liuhao.springaop.xml.aspect;

public class MyAdvice {

    public void beforeAdvice(){
        System.out.println("beforeAdvice");
    }
    public void afterAdvice(){
        System.out.println("afterAdvice");
    }
}
