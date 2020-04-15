package com.sspu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class sspuConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(sspuConfigApplication.class,args);
    }
}
