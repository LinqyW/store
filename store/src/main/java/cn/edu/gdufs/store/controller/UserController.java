package cn.edu.gdufs.store.controller;

import cn.edu.gdufs.store.controller.ex.*;
import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.entity.User;
import cn.edu.gdufs.store.service.UserService;
import cn.edu.gdufs.store.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Description:用户请求相关的控制器类
 */
@RestController
@Api(value = "v1", tags = "用户模块接口")
@RequestMapping("/users")
public class UserController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @PostMapping("reg")
    @ApiOperation(value = "用户注册", notes = "用户注册")
    public JsonResult<Void> reg(@Valid @RequestBody User user) {
        userService.reg(user);
        logger.info("user{}", user.toString());
        return new JsonResult<Void>(OK);
    }

    @PostMapping("login")
    @ApiOperation(value = "用户登录", notes = "用户登录")
    public JsonResult<String> login(@Valid @ApiParam(value = "用户名") String username, @ApiParam(value = "密码") String password, HttpServletResponse response) {
        logger.info("user{}", username);
        User data = userService.login(username, password);
//        //登录成功后，将uid和username存入到HttpSession中
//        session.setAttribute("uid", data.getUid());
//        session.setAttribute("username", data.getUsername());
        //登录成功后，返回token
        String token = userService.setToken(data.getUid(), data.getUsername(), response);
        // 将以上返回值和状态码OK封装到响应结果中并返回
        return new JsonResult<String>(OK, token);
    }

    @PutMapping("password")
    @ApiOperation(value = "用户修改密码", notes = "用户修改密码")
    public JsonResult<Void> changePassword(@ApiParam(value = "原密码") String oldPassword, @ApiParam(value = "新密码") String newPassword, HttpServletRequest request) {
//        // 调用session.getAttribute("")获取uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        UserDTO user = userService.getUidAndUsername(request);
        if (user == null)
            return new JsonResult<Void>(Fail);
        userService.changePassword(user.getUid(), user.getUsername(), oldPassword, newPassword);
        logger.info("密码修改成功！");
        return new JsonResult<Void>(OK);
    }

    @GetMapping("uid")
    @ApiOperation(value = "获取用户信息", notes = "通过用户id获取")
    public JsonResult getByUid(HttpServletRequest request) {
//        // 从HttpSession对象中获取uid
//        Integer uid = getUidFromSession(session);
        // 从token中取出uid和username
        UserDTO user = userService.getUidAndUsername(request);
        if (user == null)
            return new JsonResult<Void>(Fail);
        Integer uid = user.getUid();
        User data = userService.getByUid(uid);
        logger.info("userid{}", uid);
        return new JsonResult<User>(OK, data);
    }

    @PutMapping("info")
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    public JsonResult<Void> changeInfo(@RequestBody User user, HttpServletRequest request) {
        logger.info("userinfo{}", user.toString());
//        // 从HttpSession对象中获取uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.changeInfo(userdto.getUid(), userdto.getUsername(), user);
        return new JsonResult<Void>(OK);
    }

    /**
     * 头像文件大小的上限值(10MB)
     */
    public static final int AVATAR_MAX_SIZE = 10 * 1024 * 1024;
    /**
     * 允许上传的头像的文件类型
     */
    public static final List<String> AVATAR_TYPES = new ArrayList<String>();

    /** 初始化允许上传的头像的文件类型 */
    static {
        AVATAR_TYPES.add("image/jpg");
        AVATAR_TYPES.add("image/jpeg");
        AVATAR_TYPES.add("image/png");
        AVATAR_TYPES.add("image/bmp");
        AVATAR_TYPES.add("image/gif");
    }

    @PostMapping("avatar")
    @ApiOperation(value = "用户上传头像", notes = "用户上传头像")
    public JsonResult changeAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        // 判断上传的文件是否为空
        if (file.isEmpty()) {
            // 是：抛出异常
            throw new FileEmptyException("上传的头像文件不允许为空");
        }

        // 判断上传的文件大小是否超出限制值
        if (file.getSize() > AVATAR_MAX_SIZE) { // getSize()：返回文件的大小，以字节为单位
            // 是：抛出异常
            throw new FileSizeException("不允许上传超过" + (AVATAR_MAX_SIZE / 1024) + "KB的头像文件");
        }

        // 判断上传的文件类型是否超出限制
        String contentType = file.getContentType();
        // boolean contains(Object o)：当前列表若包含某元素，返回结果为true；若不包含该元素，返回结果为false
        if (!AVATAR_TYPES.contains(contentType)) {
            // 是：抛出异常
            throw new FileTypeException("不支持使用该类型的文件作为头像，允许的文件类型：" + AVATAR_TYPES);
        }

        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);

        // 设置用户保存头像的文件夹路径
        String parent = "./upload/" + userdto.getUid();
        System.out.println(parent);
        // 保存头像文件的文件夹
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存的头像文件的文件名
        String suffix = "";
        String originalFilename = file.getOriginalFilename();
        int beginIndex = originalFilename.lastIndexOf(".");
        if (beginIndex > 0) {
            suffix = originalFilename.substring(beginIndex);
        }
        String filename = UUID.randomUUID().toString() + suffix;

        // 创建文件对象，表示保存的头像文件
        File dest = new File(dir, filename);
        // 执行保存头像文件
        try {
            file.transferTo(dest);
        } catch (IllegalStateException e) {
            // 抛出异常
            throw new FileStateException("文件状态异常，可能文件已被移动或删除");
        } catch (IOException e) {
            // 抛出异常
            throw new FileUploadIOException("上传文件时读写错误，请稍后重新尝试");
        }

        // 头像路径
        String avatar = parent + "/" + filename;
//        // 从Session中获取uid和username
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        // 将头像写入到数据库中
        userService.changeAvatar(uid, username, avatar);
        logger.info("头像修改成功！");
        // 返回成功头像路径
        return new JsonResult<String>(OK, avatar);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "用户退出登录", notes = "用户退出登录")
    public JsonResult<Void> logout(HttpServletRequest request) {
//        Integer uid = getUidFromSession(session);
//        String username = getUsernameFromSession(session);
        // 从token中取出uid和username
        UserDTO userdto = userService.getUidAndUsername(request);
        if (userdto == null)
            return new JsonResult<Void>(Fail);
        userService.logout(request);
        Integer uid = userdto.getUid();
        String username = userdto.getUsername();
        logger.info("userid{},username{}退出登录！", uid, username);
//        session.setAttribute("uid", null);
//        session.setAttribute("username", null);
        return new JsonResult<Void>(OK);
    }

}
