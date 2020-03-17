package com.sspu.cart.controller;

import com.sspu.cart.pojo.Cart;
import com.sspu.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        //当加入购物车的时候,通过此方法加入redis
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }


    //合并购物车和localstorge
    @PostMapping("merge")
    public ResponseEntity<Void> mergeCart(@RequestBody List<Cart> carts) {


        //当加入购物车的时候,通过此方法加入redis
        this.cartService.mergeCart(carts);
        return ResponseEntity.ok().build();
    }
    //已登录,通过此方法来查询redis 购物车
    @GetMapping("queryCarts")
    public ResponseEntity<List<Cart>> queryCarts() {

        List<Cart> carts = this.cartService.queryCarts();

        if (CollectionUtils.isEmpty(carts)) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carts);
    }

    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{skuId")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId){
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}
