package lcom.leyou.auth.service;

import com.leyou.user.pojo.User;
import lcom.leyou.auth.client.UserClient;
import lcom.leyou.auth.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserInfo;
import utils.JwtUtils;

/**
 * @author hp
 */
@Service
public class AuthService {

    @Autowired
    UserClient userClient;
    @Autowired
    JwtProperties jwtProperties;

    public String accredit(String username, String password) {
        //1. 利用FeignClient 调用接口进行查询用户是否存在
        User user = this.userClient.queryUser(username, password);

        //2.判断user是否存在
        if (user == null) {
            return null;
        }
        try {

            //3.利用jwtUtils生成jwt类型的token
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            return JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
