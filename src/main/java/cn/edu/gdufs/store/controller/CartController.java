package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.service.CartService;
import cn.edu.gdufs.store.service.UserService;
import cn.edu.gdufs.store.util.JsonResult;
import cn.edu.gdufs.store.vo.CartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Description:用户购物车相关的控制器类
 */
@RestController
@Api(value = "v1", tags = "用户购物车模块接口")
@RequestMapping("carts")
public class CartController extends BaseController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    /**
     * 将商品添加到购物车
     *
     * @param pid
     * @param amount
     * @param request
     * @return
     */
    @PostMapping({"", "/"})
    @ApiOperation(value = "将商品添加到购物车", notes = "将商品添加到购物车")
    public JsonResult<Void> addToCart(Integer pid, Integer amount, HttpServletRequest request) {
        System.out.println("pid=" + pid);
        System.out.println("amount=" + amount);
//        // 从Session中获取uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中获取uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        // 调用业务对象执行添加到购物车
        cartService.addToCart(uid, pid, amount, username);
        // 返回成功
        return new JsonResult<Void>(OK);
    }

    /**
     * 查询某用户的购物车数据
     *
     * @param request
     * @return
     */
    @GetMapping({"", "/"})
    @ApiOperation(value = "查询某用户的购物车数据", notes = "查询某用户的购物车数据")
    public JsonResult getVOByUid(HttpServletRequest request) {
//        // 从Session中获取uid
//        Integer uid = getUidFromSession(session);
        // 从token中获取uid
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        // 调用业务对象执行查询数据
        List<CartVO> data = cartService.getVOByUid(uid);
        // 返回成功与数据
        return new JsonResult<List<CartVO>>(OK, data);
    }

    /**
     * 将购物车中某商品的数量加1
     *
     * @param cid
     * @param request
     * @return
     */
    @PutMapping("{cid}")
    @ApiOperation(value = "将购物车中某商品的数量加1", notes = "将购物车中某商品的数量加1")
    public JsonResult addNum(@PathVariable("cid") Integer cid, HttpServletRequest request) {
//        // 从Session中获取uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中获取uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        // 调用业务对象执行增加数量
        Integer data = cartService.addNum(cid, uid, username);
        // 返回成功
        return new JsonResult<Integer>(OK, data);
    }

    /**
     * 根据若干个购物车数据id查询详情的列表
     *
     * @param cids
     * @param request
     * @return
     */
    @GetMapping("list")
    @ApiOperation(value = "根据若干个购物车数据id查询详情的列表", notes = "根据若干个购物车数据id查询详情的列表")
    public JsonResult getVOByCids(Integer[] cids, HttpServletRequest request) {
//        // 从Session中获取uid
//        Integer uid = getUidFromSession(session);
        // 从token中取出uid
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        // 调用业务对象执行查询数据
        List<CartVO> data = cartService.getVOByCids(uid, cids);
        // 返回成功与数据
        return new JsonResult<>(OK, data);
    }
}
