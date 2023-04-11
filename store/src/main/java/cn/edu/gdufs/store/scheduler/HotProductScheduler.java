package cn.edu.gdufs.store.scheduler;

import cn.edu.gdufs.store.dto.PriorityDTO;
import cn.edu.gdufs.store.entity.Product;
import cn.edu.gdufs.store.mapper.ProductMapper;
import cn.edu.gdufs.store.util.RedisConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:定时任务：定时自动刷新redis中的数据，保证数据一致性
 * Param:
 * return:
 * Author:lqy
 * Date:2023/4/10
 */
@Component
public class HotProductScheduler {
    private static final Logger log = LoggerFactory.getLogger(HotProductScheduler.class);

    @Resource
    private ProductMapper productMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    //此处表示，每 20秒执行一次
    @Scheduled(cron = "0/20 * * * * ?")
    public void sortLikesScheduler() {
        //定时执行以下命令
        this.cacheSortResult();
        System.out.println("定时任务--刷新redis");
    }

    //异步方法：@Async是Spring的注解，可以加在类或方法上。
    // 如果加上了这个注解，那么该类或者该方法在使用时将会进行异步处理，
    // 也就是创建一个线程来实现这个类或者方法，实现多线程。
    @Async("threadPoolTaskExecutor")
    void cacheSortResult() {
        try {
            ZSetOperations<String, PriorityDTO> zSetOperations = redisTemplate.opsForZSet();
            List<Product> list = productMapper.findHotList();
            if (list != null && !list.isEmpty()) {
                redisTemplate.delete(RedisConstants.RedisSortedSetKey);
                list.forEach(product -> {
                    PriorityDTO dto = new PriorityDTO(product.getId());
                    zSetOperations.add(RedisConstants.RedisSortedSetKey, dto, product.getPriority());
                });
            }
        } catch (Exception e) {
            log.error("发生异常：", e.fillInStackTrace());
        }
    }
}
