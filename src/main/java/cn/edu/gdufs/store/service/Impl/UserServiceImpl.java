package cn.edu.gdufs.store.service.Impl;

import cn.edu.gdufs.store.dto.UserDTO;
import cn.edu.gdufs.store.entity.User;
import cn.edu.gdufs.store.mapper.UserMapper;
import cn.edu.gdufs.store.service.UserService;
import cn.edu.gdufs.store.service.ex.*;
import cn.edu.gdufs.store.util.JwtUtil;
import cn.edu.gdufs.store.util.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Description:处理用户数据的业务层实现类
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void reg(User user) {
        // 根据参数user对象获取注册的用户名
        String username = user.getUsername();
        // 调用持久层的User findByUsername(String username)方法，根据用户名查询用户数据
        User result = userMapper.findByUsername(username);
        // 判断查询结果是否不为null
        if (result != null) {
            // 是：表示用户名已被占用，则抛出UsernameDuplicateException异常
            throw new UsernameDuplicateException("该用户名已被注册！");
        }
        String phone = user.getPhone();
        String email = user.getEmail();

        if (phone != null) {
            // 校验手机号格式
            if (RegexUtil.isPhoneInvalid(phone)) {
                // 否：抛出异常
                throw new UserPhoneNumberFormatErrorException("手机号格式错误！");
            }
        }
        if (email != null) {
            //校验邮箱格式
            if (!RegexUtil.isEmailInvalid(email)) {
                //否：抛出异常
                throw new UserEmailFormatErrorException("邮箱格式错误！");
            }
        }
        // 创建当前时间对象
        Date now = new Date();
        // 补全数据：加密后的密码
        String salt = UUID.randomUUID().toString().toUpperCase();
        String md5Password = getMd5Password(user.getPassword(), salt);
        user.setPassword(md5Password);
        // 补全数据：盐值
        user.setSalt(salt);
        // 补全数据：isDelete(0)
        user.setIsDelete(0);
        // 补全数据：4项日志属性
        user.setCreatedUser(username);
        user.setCreatedTime(now);
        user.setModifiedUser(username);
        user.setModifiedTime(now);

        // 表示用户名没有被占用，则允许注册
        // 调用持久层Integer insert(User user)方法，执行注册并获取返回值(受影响的行数)
        Integer rows = userMapper.insert(user);
        // 判断受影响的行数是否不为1
        if (rows != 1) {
            // 是：插入数据时出现某种错误，则抛出InsertException异常
            throw new InsertException("添加用户数据失败！");
        }
    }

    /**
     * 执行密码加密
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密文
     */
    private String getMd5Password(String password, String salt) {
        /*
         * 加密规则：
         * 1、无视原始密码的强度
         * 2、使用UUID作为盐值，在原始密码的左右两侧拼接
         * 3、循环加密3次
         */
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();
        }
        return password;
    }

    @Override
    public User login(String username, String password) {
        // 调用userMapper的findByUsername()方法，根据参数username查询用户数据
        User result = userMapper.findByUsername(username);
        // 判断查询结果是否为null
        if (result == null) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 判断查询结果中的isDelete是否为1
        if (result.getIsDelete() == 1) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 从查询结果中获取盐值
        String salt = result.getSalt();
        // 调用getMd5Password()方法，将参数password和salt结合起来进行加密
        String md5Password = getMd5Password(password, salt);
        // 判断查询结果中的密码，与以上加密得到的密码是否不一致
        if (!result.getPassword().equals(md5Password)) {
            // 是：抛出PasswordNotMatchException异常
            throw new PasswordNotMatchException("密码验证失败！");
        }

        // 创建新的User对象
        User user = new User();
        // 将查询结果中的uid、username、avatar封装到新的user对象中
        user.setUid(result.getUid());
        user.setUsername(result.getUsername());
        user.setAvatar(result.getAvatar());
        // 返回新的user对象
        return user;
    }

    @Override
    public void changePassword(Integer uid, String username, String oldPassword, String newPassword) {
        // 调用userMapper的findByUid()方法，根据参数uid查询用户数据
        User result = userMapper.findByUid(uid);
        // 检查查询结果是否为null
        if (result == null) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 检查查询结果中的isDelete是否为1
        if (result.getIsDelete().equals(1)) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 从查询结果中取出盐值
        String salt = result.getSalt();
        // 将参数oldPassword结合盐值加密，得到oldMd5Password
        String oldMd5Password = getMd5Password(oldPassword, salt);
        // 判断查询结果中的password与oldMd5Password是否不一致
        if (!result.getPassword().contentEquals(oldMd5Password)) {
            // 是：抛出PasswordNotMatchException异常
            throw new PasswordNotMatchException("原密码错误！");
        }

        // 将参数newPassword结合盐值加密，得到newMd5Password
        String newMd5Password = getMd5Password(newPassword, salt);
        // 创建当前时间对象
        Date now = new Date();
        // 调用userMapper的updatePasswordByUid()更新密码，并获取返回值
        Integer rows = userMapper.updatePasswordByUid(uid, newMd5Password, username, now);
        // 判断以上返回的受影响行数是否不为1
        if (rows != 1) {
            // 是：抛出UpdateException异常
            throw new UpdateException("更新用户数据失败！");
        }
    }

    @Override
    public User getByUid(Integer uid) {
        // 调用userMapper的findByUid()方法，根据参数uid查询用户数据
        User result = userMapper.findByUid(uid);
        // 判断查询结果是否为null
        if (result == null) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在!");
        }

        // 判断查询结果中的isDelete是否为1
        if (result.getIsDelete().equals(1)) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 创建新的User对象
        User user = new User();
        // 将以上查询结果中的username/phone/email/gender封装到新User对象中
        user.setUsername(result.getUsername());
        user.setPhone(result.getPhone());
        user.setEmail(result.getEmail());
        user.setGender(result.getGender());

        // 返回新的User对象
        return user;
    }

    @Override
    public void changeInfo(Integer uid, String username, User user) {
        // 调用userMapper的findByUid()方法，根据参数uid查询用户数据
        User result = userMapper.findByUid(uid);
        // 判断查询结果是否为null
        if (result == null) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        // 判断查询结果中的isDelete是否为1
        if (result.getIsDelete().equals(1)) {
            // 是：抛出UserNotFoundException异常
            throw new UserNotFoundException("该用户不存在！");
        }

        String phone = user.getPhone();
        String email = user.getEmail();

        if (phone != null) {
            // 校验手机号格式
            if (RegexUtil.isPhoneInvalid(phone)) {
                // 否：抛出异常
                throw new UserPhoneNumberFormatErrorException("手机号格式错误！");
            }
        }
        if (email != null) {
            //校验邮箱格式
            if (!RegexUtil.isEmailInvalid(email)) {
                //否：抛出异常
                throw new UserEmailFormatErrorException("邮箱格式错误！");
            }
        }
        // 向参数user中补全数据：uid
        user.setUid(uid);
        // 向参数user中补全数据：modifiedUser(username)
        user.setModifiedUser(username);
        // 向参数user中补全数据：modifiedTime(new Date())
        user.setModifiedTime(new Date());
        // 调用userMapper的updateInfoByUid(User user)方法执行修改，并获取返回值
        Integer rows = userMapper.updateInfoByUid(user);
        // 判断以上返回的受影响行数是否不为1
        if (rows != 1) {
            // 是：抛出UpdateException异常
            throw new UpdateException("更新用户数据失败！");
        }
    }

    @Override
    public void changeAvatar(Integer uid, String username, String avatar) {
        // 调用userMapper的findByUid()方法，根据参数uid查询用户数据
        User result = userMapper.findByUid(uid);
        // 检查查询结果是否为null
        if (result == null) {
            // 是：抛出UserNotFoundException
            throw new UserNotFoundException("该用户不存在！");
        }

        // 检查查询结果中的isDelete是否为1
        if (result.getIsDelete().equals(1)) {
            // 是：抛出UserNotFoundException
            throw new UserNotFoundException("该用户不存在！");
        }

        // 创建当前时间对象
        Date now = new Date();
        // 调用userMapper的updateAvatarByUid()方法执行更新，并获取返回值
        Integer rows = userMapper.updateAvatarByUid(uid, avatar, username, now);
        // 判断以上返回的受影响行数是否不为1
        if (rows != 1) {
            // 是：抛出UpdateException
            throw new UpdateException("更新用户数据失败！");
        }
    }

    @Override
    public String setToken(Integer uid, String username, HttpServletResponse response) {
        //1.根据用户信息，创建token
        String token = JwtUtil.createToken(uid, username);

        //2.将token写入redis,并设置有效期
        String tokenKey = "user:" + uid;
        redisTemplate.opsForValue().set(tokenKey, token);
        // 设置token有效期
        redisTemplate.expire(tokenKey, 30, TimeUnit.MINUTES);

        //3.将token写入 http响应头
        // response.addHeader("Access-Control-Expose-Headers","token");
        response.addHeader("token", token);

        //4.返回token
        return "token:" + token;
    }

    @Override
    public UserDTO getUidAndUsername(HttpServletRequest request) {
        // 1.从请求头中获取token,并解析出用户id和username
        Integer uidFromRequest = JwtUtil.getUid(request.getHeader("token"));
        String usernameFromRequest = JwtUtil.getUsername(request.getHeader("token"));
        System.out.println(uidFromRequest);
        System.out.println(usernameFromRequest);

        // 2.从redis中获取 token
        String tokenRedis = (String) redisTemplate.opsForValue().get("user:" + uidFromRequest);
        Integer uidFromRedis = JwtUtil.getUid(tokenRedis);
        System.out.println(uidFromRedis);

        // 3.判断用户在redis是否存在，即判断是否已经登陆
        if (uidFromRedis == null) {
            // 不存在，报错
            return null;
        }

        UserDTO user = new UserDTO();
        user.setUid(uidFromRequest);
        user.setUsername(usernameFromRequest);
        return user;
    }

    @Override
    public void logout(HttpServletRequest request) {
        // 1.从请求头中获取token,并解析出用户id
        Integer uid = JwtUtil.getUid(request.getHeader("token"));
        System.out.println(uid);

        // 2.从redis中删除该对应的键
        redisTemplate.delete("user:" + uid);
    }

}
