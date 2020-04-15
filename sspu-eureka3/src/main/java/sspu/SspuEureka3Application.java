package sspu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SspuEureka3Application {
    public static void main(String[] args) {
        SpringApplication.run(SspuEureka3Application.class, args);
    }
}
