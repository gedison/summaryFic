package com.pastelpunk.summaryfic.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.pastelpunk.summaryfic")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public ServletRegistrationBean servletRegistrationBean(){
//        ServletRegistrationBean servlet = new ServletRegistrationBean(
//                new CamelHttpTransportServlet(), "/api/v1/camel/*");
//        servlet.setName("CamelServlet");
//        return servlet;
//    }

}
