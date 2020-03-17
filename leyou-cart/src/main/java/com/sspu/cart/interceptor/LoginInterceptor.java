package com.sspu.cart.interceptor;


import com.leyou.item.utils.CookieUtils;
import com.sspu.cart.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pojo.UserInfo;
import utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**登录拦截器,检验是否登录
 * @author hp
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {
private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();

@Autowired
private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        //解析token,获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());

        if (userInfo == null){
            return false;
        }
        //把userInfo 放入线程局部变量
        THREAD_LOCAL.set(userInfo);

        return true;

    }
    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的局部变量,因为我们使用的是tomcat的线程池,线程不会结束,也就不会释放线程的局部变量
        THREAD_LOCAL.remove();
    }
}
