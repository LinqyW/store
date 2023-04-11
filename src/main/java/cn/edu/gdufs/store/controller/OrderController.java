package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.entity.Order;
import cn.edu.gdufs.store.service.OrderService;
import cn.edu.gdufs.store.service.UserService;
import cn.edu.gdufs.store.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Description:订单相关的控制器类
 */
@RestController
@Api(value = "v1", tags = "用户订单模块接口")
@RequestMapping("orders")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * 创建订单
     *
     * @param aid
     * @param cids
     * @param request
     * @return
     */
    @PostMapping({"", "/"})
    @ApiOperation(value = "用户创建订单", notes = "用户创建订单")
    public JsonResult create(Integer aid, Integer[] cids, HttpServletRequest request) {
//        // 从Session中取出uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        // 调用业务对象执行业务
        Order data = orderService.create(aid, cids, uid, username);
        // 返回成功与数据
        return new JsonResult<Order>(OK, data);
    }

    /**
     * 取消订单
     *
     * @param oid
     * @param request
     * @return
     */
    @PutMapping({"", "/"})
    @ApiOperation(value = "用户取消订单", notes = "用户取消订单")
    public JsonResult<Void> cancel(Integer oid, HttpServletRequest request) {
//        // 从Session中取出uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        // 调用业务对象执行业务
        orderService.cancel(oid, uid, username);
        // 返回成功与数据
        return new JsonResult<Void>(OK);
    }
}
