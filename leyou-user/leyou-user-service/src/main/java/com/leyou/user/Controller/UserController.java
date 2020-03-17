package com.leyou.user.Controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;

import javax.validation.Valid;

/**
 * 校验数据是否可用
 *
 * @author hp
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * @param data 检测数据是否可用
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = this.userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }


    /*

     */
    @PostMapping("code")
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {

        this.userService.sendVerifyCode(phone);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    注册
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        Boolean boo = this.userService.register(user, code);

        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    查询用户
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password) {

        User user=this.userService.queryUser(username,password);

        if (user==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);

    }
}
