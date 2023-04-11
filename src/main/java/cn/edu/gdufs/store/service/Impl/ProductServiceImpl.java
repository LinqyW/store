package cn.edu.gdufs.store.service.Impl;

import cn.edu.gdufs.store.dto.PriorityDTO;
import cn.edu.gdufs.store.entity.Product;
import cn.edu.gdufs.store.mapper.ProductMapper;
import cn.edu.gdufs.store.service.ProductService;
import cn.edu.gdufs.store.service.ex.ProductNotFoundException;
import cn.edu.gdufs.store.util.RedisConstants;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Description:处理商品数据的业务层实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Product> findHotList() {
        List<Product> list = Lists.newLinkedList();
        final String key = RedisConstants.RedisSortedSetKey;
        ZSetOperations<String, PriorityDTO> zSetOperations = redisTemplate.opsForZSet();
        final Long size = zSetOperations.size(key);
        //反向取出所有数据，包括分数
        Set<ZSetOperations.TypedTuple<PriorityDTO>> set =
                zSetOperations.reverseRangeWithScores(key, 0L, size);
        if (set != null && !set.isEmpty()) {
            set.forEach(tuple -> {
                Product product = new Product();
                product.setPriority((BigDecimal.valueOf(tuple.getScore())).intValue());
                product.setId(tuple.getValue().getId());
                product.setPriority(null);
                product.setCreatedUser(null);
                product.setCreatedTime(null);
                product.setModifiedUser(null);
                product.setModifiedTime(null);
                list.add(product);
            });
        }
        return list;
//        List<Product> list = productMapper.findHotList();
//        for (Product product : list) {
//            product.setPriority(null);
//            product.setCreatedUser(null);
//            product.setCreatedTime(null);
//            product.setModifiedUser(null);
//            product.setModifiedTime(null);
//        }
//        return list;
    }

    @Override
    public Product findById(Integer id) {
        // 根据参数id调用私有方法执行查询，获取商品数据
        Product product = productMapper.findById(id);
        // 判断查询结果是否为null
        if (product == null) {
            // 是：抛出ProductNotFoundException
            throw new ProductNotFoundException("尝试访问的商品数据不存在！");
        }
        // 将查询结果中的部分属性设置为null
        product.setPriority(null);
        product.setCreatedUser(null);
        product.setCreatedTime(null);
        product.setModifiedUser(null);
        product.setModifiedTime(null);
        // 返回查询结果
        return product;
    }
}
