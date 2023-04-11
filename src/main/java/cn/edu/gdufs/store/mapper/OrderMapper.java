package cn.edu.gdufs.store.mapper;

import cn.edu.gdufs.store.entity.Order;
import cn.edu.gdufs.store.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * Description:处理订单及订单商品数据的持久层接口
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     *
     * @param order 订单数据
     * @return 受影响的行数
     */
    Integer insertOrder(Order order);

    /**
     * 插入订单商品数据
     *
     * @param orderItem 订单商品数据
     * @return 受影响的行数
     */
    Integer insertOrderItem(OrderItem orderItem);

    /**
     * 删除订单数据
     *
     * @param oid 订单号
     * @return 受影响的行数
     */
    Integer cancelOrder(@Param("oid") Integer oid, @Param("modifiedUser") String modifiedUser, @Param("modifiedTime")
            Date modifiedTime);

    /**
     * 删除订单商品数据
     *
     * @param oid 订单号
     * @return
     */
    Integer cancelOrderItem(Integer oid);

    /**
     * 查找订单数据
     *
     * @param oid 订单号
     * @return 受影响的行数
     */
    Order findByOid(Integer oid);
}
