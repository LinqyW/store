package cn.edu.gdufs.store.service;

import cn.edu.gdufs.store.entity.Order;

/**
 * Description:处理订单和订单数据的业务层接口
 */
public interface OrderService {
    /**
     * 创建订单
     * @param aid 收货地址的id
     * @param cids 即将购买的商品数据在购物车表中的id
     * @param uid 当前登录的用户的id
     * @param username 当前登录的用户名
     * @return 成功创建的订单数据
     */
    Order create(Integer aid, Integer[] cids, Integer uid, String username);

    /**
     * 取消订单
     * @param oid 订单id
     * @param uid 用户id
     * @param username 用户名
     */
    void cancel(Integer oid, Integer uid, String username);
}
