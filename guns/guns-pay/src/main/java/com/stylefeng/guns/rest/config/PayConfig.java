package com.stylefeng.guns.rest.config;

import com.alipay.demo.trade.Main;
import com.cskaoyan.component.AliyunComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayConfig {
    // 注册组件，quanllong
    @Bean
    public Main main(){
        return new Main();
    }

    @Bean
    public AliyunComponent aliyunComponent(){
        return new AliyunComponent();
    }
}
