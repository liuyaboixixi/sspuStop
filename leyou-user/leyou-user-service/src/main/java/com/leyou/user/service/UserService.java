package com.leyou.user.service;

import com.leyou.item.utils.CodecUtils;
import com.leyou.item.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;


import com.leyou.user.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * 校验数据是否可用
 *
 * @author hp
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String key_prefix = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //1.生成验证码
        String code = NumberUtils.generateCode(6);
        //String code = "老爸好,哈哈哈哈";
        //2.发送mq消息
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);

        try {
            this.amqpTemplate.convertAndSend("leyou.sms.exchange", "verifycode.sms", msg);
        } catch (AmqpException e) {
            System.out.println("111");
        }

        //3.把验证码保存在redis中
        this.redisTemplate.opsForValue().set(key_prefix + phone, code, 5, TimeUnit.MINUTES);
        System.out.println("发送成功");
    }

    public boolean register(User user, String code) {
        //校验验证码短信
        String cacheCode = this.redisTemplate.opsForValue().get(key_prefix + user.getPhone());
        if (!StringUtils.equals(code, cacheCode)) {
            return false;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //对密码进行加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        //插入到数据库
        int i = this.userMapper.insertSelective(user);
        if (i == 1) {
            System.out.println("注册插入用户成功");
            return true;
        }
        return false;
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //校验用户名
        if (user == null)
        {
            return null;
        }
        String md5HexPassword = CodecUtils.md5Hex(password, user.getSalt());
        //校验密码
        if (!user.getPassword().equals(md5HexPassword)){
            return null;
        }
        //验证通过
        return  user;


    }
}
