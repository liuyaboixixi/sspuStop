package com.leyou.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableEurekaClient
//@ComponentScan(basePackages = {"com.leyou.*"})
@MapperScan("com.leyou.item.mapper")
public class SspuItemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SspuItemServiceApplication.class, args);
    }
}
