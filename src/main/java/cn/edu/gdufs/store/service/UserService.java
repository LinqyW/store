package cn.edu.gdufs.store.service;

import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:处理用户数据的业务层接口
 */

public interface UserService {
    /**
     * 用户注册
     */
    void reg(User user);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 修改密码
     */
    public void changePassword(Integer uid, String username, String oldPassword, String newPassword);

    /**
     * 获取当前登录的用户的信息
     */
    User getByUid(Integer uid);

    /**
     * 修改用户资料
     */
    void changeInfo(Integer uid, String username, User user);

    /**
     * 修改用户头像
     */
    void changeAvatar(Integer uid, String username, String avatar);

    /**
     *获取token
     */
    String setToken(Integer uid, String username, HttpServletResponse response);

    /**
     *从token中获取uid和username
     */
    UserDTO getUidAndUsername(HttpServletRequest request);

    /**
     * 用户退出登录
     */
    void logout(HttpServletRequest request);
}
