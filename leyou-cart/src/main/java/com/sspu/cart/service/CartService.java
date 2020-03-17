package com.sspu.cart.service;

import com.leyou.item.Sku;
import com.leyou.item.utils.JsonUtils;
import com.sspu.cart.GoodsClient;
import com.sspu.cart.interceptor.LoginInterceptor;
import com.sspu.cart.pojo.Cart;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pojo.UserInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 添加购物车业务的
 *
 * @author hp
 */
@Service
public class CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;


    private static final String KEY_PREFIX = "user:cart";

    public void addCart(Cart cart) {
        //查询购物车记录
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断当亲的商品手在购物车上
        BoundHashOperations<String, Object, Object> hashOperations = redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //首先判断当前的商品在不在购物车里
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        Boolean aBoolean = hashOperations.hasKey(key);
        if (aBoolean) {
            //如果在 更新数量
            String cartJson = hashOperations.get(key).toString();
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);


        } else {
            //如果不在,新增购物车
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" :
                    StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
        }

        //更新到redis
        hashOperations.put(key, JsonUtils.serialize(cart));

    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //如果用户没有购物记录,直接返回
        if (!this.redisTemplate.hasKey(KEY_PREFIX + userInfo.getId())) {
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        List<Object> cartsJson = hashOperations.values();

        //如果购物车为空  直接返回
        if (CollectionUtils.isEmpty(cartsJson)) {
            return null;
        }

        //把List<object> 转化为  List<Cart> 集合

        return  cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());
    }

    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //如果用户没有购物记录,直接返回
        if (!this.redisTemplate.hasKey(KEY_PREFIX + userInfo.getId())) {
            return ;
        }

        Integer num = cart.getNum();
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        String cartJson = hashOperations.get(cart.getSkuId().toString()).toString();
        cart = JsonUtils.parse(cartJson, Cart.class);
        cart.setNum(num);


        hashOperations.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        //通过线程变量 获取登录的用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        hashOperations.delete(KEY_PREFIX+skuId);
    }

    public void mergeCart(List<Cart> carts) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //如果用户没有购物记录,直接返回
        if (!this.redisTemplate.hasKey(KEY_PREFIX + userInfo.getId())) {
            return ;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> hashOperations = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        List<Object> cartsJson = hashOperations.values();
        //如果购物车为空  直接返回
        if (CollectionUtils.isEmpty(cartsJson)) {
            return ;
        }else{
            List<Cart> cartList = cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(), Cart.class)).collect(Collectors.toList());
            for (Cart cart : carts) {//localstorge
                boolean temp=true;
                for (Cart cart1 : cartList) {
                    if (cart.getSkuId().equals(cart1.getSkuId())){
                        this.updateNum(cart);
                        temp=false;
                    }
                }
                if (temp){
                        this.addCart(cart);

                }


            }
        }
    }
}
