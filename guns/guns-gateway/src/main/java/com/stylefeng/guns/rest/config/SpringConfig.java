package com.stylefeng.guns.rest.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringConfig implements WebMvcConfigurer {

    // 首先从容器中取出原来的ConversionService
    /*@Autowired
    ConfigurableConversionService conversionService;
    // 给他加上我们自定义的conversion
    @PostConstruct
    public void addCustomConverter(){
        conversionService.addConverter(new StringArray2String());
    }
    // 重新注册进去
    @Bean
    @Primary
    public ConfigurableConversionService conversionService(){
        return conversionService;
    }*/

    @Value("${disk.path}")
    private String path;

    @Override
    @ConfigurationProperties(prefix = "disk")
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // String path;
        registry.addResourceHandler("/pic/**").addResourceLocations("file:" + path);
    }
    /*localhost:8080/wx/storage/fetch/ imgPre     g:/spring_mall/adbcefg.jpg
    adbcefg.jpg   qRCodeAddress

    localhost:8080/wx/storage/fetch/adbcefg.jpg     g:/spring_mall/adbcefg.jpg*/

    // 图片和文件上传组件
    /*@Bean()
    public CommonsMultipartResolver multipartResolver(){
        return new CommonsMultipartResolver();
    }*/
}
