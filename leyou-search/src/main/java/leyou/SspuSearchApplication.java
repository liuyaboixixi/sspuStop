package leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author hp
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SspuSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SspuSearchApplication.class, args);
    }
}
