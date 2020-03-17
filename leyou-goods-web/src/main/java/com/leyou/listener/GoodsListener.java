package com.leyou.listener;

import com.leyou.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理insert和update的消息
 *
 * @author hp
 */
@Component
//@Component  类上的注解，注册到Spring容器中，当没有架构分析的时候，使用此注解
public class GoodsListener {
    @Autowired
    GoodsHtmlService goodsHtmlService;

    //@RabbitListenner 方法上的注解声明这个方法是一个消费者方法
    @RabbitListener(bindings = @QueueBinding(
            ///bindings：指定绑定关系，可以有多个。值是@QueueBinding的数组。
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    /*@QueueBinding包含下面属性：
     - value：这个消费者关联的队列。值是@Queue，代表一个队列
- exchange：队列所绑定的交换机，值是@Exchange类型
- key：队列和交换机绑定的RoutingKey
*/
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.createHtml(id);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.item.delete.queue", durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
            type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
