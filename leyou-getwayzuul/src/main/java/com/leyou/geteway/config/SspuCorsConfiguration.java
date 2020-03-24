package com.leyou.geteway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SspuCorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //2允许的域
        config.addAllowedOrigin("http://manage.sspu.nat300.top");
        config.addAllowedOrigin("*");
        config.setAllowCredentials(true); //允许携带cookie
        //3.是否发出Cookie 信息
        config.addAllowedMethod("*");

        //允许的头信息
        config.addAllowedHeader("*");

        //2.添加映射路径，我们拦截一切请求,初始化配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configurationSource);

    }
}
