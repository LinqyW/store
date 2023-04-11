package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.entity.Address;
import cn.edu.gdufs.store.service.AddressService;
import cn.edu.gdufs.store.service.UserService;
import cn.edu.gdufs.store.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Description:用户收货地址相关的控制器类
 */
@RestController
@Api(value = "v1", tags = "用户收货地址模块接口")
@RequestMapping("/addresses")
public class AddressController extends BaseController {
    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    /**
     * 当前用户新增收货地址
     *
     * @param address
     * @param request
     * @return
     */
    @PostMapping("address")
    @ApiOperation(value = "当前用户新增收货地址", notes = "当前用户新增收货地址")
    public JsonResult<Void> addNewAddress(Address address, HttpServletRequest request) {
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
        // 调用业务对象的方法执行业务
        addressService.addNewAddress(uid, username, address);
        // 响应成功
        return new JsonResult<Void>(OK);
    }

    /**
     * 获取当前用户的所有收货地址
     *
     * @param request
     * @return
     */
    @GetMapping({"", "/"})
    @ApiOperation(value = "获取当前用户的所有收货地址", notes = "获取当前用户的所有收货地址")
    public JsonResult getByUid(HttpServletRequest request) {
//        Integer uid = getUidFromSession(session);
        // 从token中获取uid
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        List<Address> data = addressService.getByUid(uid);
        return new JsonResult<List<Address>>(OK, data);
    }

    /**
     * 设置当前用户的默认收货地址
     *
     * @param aid
     * @param request
     * @return
     */
    @PutMapping("{aid}")
    @ApiOperation(value = "设置当前用户的默认收货地址", notes = "设置当前用户的默认收货地址")
    public JsonResult<Void> setDefault(@PathVariable("aid") Integer aid, HttpServletRequest request) {
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中获取uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        addressService.setDefault(aid, uid, username);
        return new JsonResult<Void>(OK);
    }

    /**
     * 删除当前用户指定的收货地址
     *
     * @param aid
     * @param request
     * @return
     */
    @DeleteMapping("{aid}")
    @ApiOperation(value = "删除当前用户指定的收货地址", notes = "删除当前用户指定的收货地址")
    public JsonResult<Void> delete(@PathVariable("aid") Integer aid, HttpServletRequest request) {
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中获取uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        addressService.delete(aid, uid, username);
        return new JsonResult<Void>(OK);
    }

    /**
     * 根据收货地址id获取收货地址
     *
     * @param request
     * @return
     */
    @GetMapping("{aid}")
    @ApiOperation(value = "根据收货地址id获取收货地址", notes = "根据收货地址id获取收货地址")
    public JsonResult getByAid(@PathVariable("aid") Integer aid, HttpServletRequest request) {
//        Integer uid = getUidFromSession(session);
        // 从token中获取uid
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        Address data = addressService.getByAid(aid, uid);
        return new JsonResult<Address>(OK, data);
    }
}
