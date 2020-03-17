package leyou.search.listener;

import leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 监听对象   在索引库里保存对应的信息
 *
 * @author hp
 */
@Component
public class GoodsListener {
    @Autowired
    SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.search.save.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true"
                    , type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) throws IOException {
        //spring 会自动的对  @RabbitListener  使用AOP进行监听，如果此方法有抛出异常，就不会进行ACK，如果没有抛出异常，就会ACK确认机制

        if (id == null) {
            return;
        }
        this.searchService.save(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.search.delete.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        this.searchService.delete(id);
    }
}
