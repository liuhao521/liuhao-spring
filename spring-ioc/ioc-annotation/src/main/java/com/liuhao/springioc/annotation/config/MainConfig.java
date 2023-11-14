package com.liuhao.springioc.annotation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan({"com.liuhao.springioc.annotation"})
@Import(OtherConfig.class)
public class MainConfig {


}
