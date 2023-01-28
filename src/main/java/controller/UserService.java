package controller;

import dao.UserDao;
import model.User;
import utils.ToolUtils;

import java.util.Objects;

public abstract class UserService {
    //注册，返回注册失败原因。若返回null则表示注册成功。
    public static String register(User user) throws Exception {
        if (!UserCheckService.checkUsernameLengthOk(user.username)) {
            return "用户名长度不合法";
        } else if (!UserCheckService.checkNicknameLengthOk(user.nickname)) {
            return "昵称长度不合法";
        } else if (!UserCheckService.checkPasswordLengthOk(user.password)) {
            return "密码长度不合法";
        } else if (UserCheckService.checkUsernameExist(user.username)) {
            return "用户名已存在";
        } else if (UserCheckService.checkNicknameExist(user.nickname)) {
            return "昵称已存在";
        } else {
            user.password = ToolUtils.getSha256Str(user.password);
            UserDao.addUser(user);
            return null;
        }
    }

    public static User login(String username, String password) throws Exception {
        User user = UserDao.getUserByUsername(username);
        if (user == null || !user.password.equals(ToolUtils.getSha256Str(password))) {
            return null;
        }
        return user;
    }

    public static User getOther(int id) throws Exception {
        User user = UserDao.getUserById(id);
        if (user == null) {
            return null;
        }
        user.password = null;
        user.username = null;
        return user;
    }

    public static String updateInfo(User user, String oldNickname) throws Exception {
        if (!UserCheckService.checkNicknameLengthOk(user.nickname)) {
            return "昵称长度不合法";
        }
        User queryUser = UserDao.getUserByNickname(user.nickname);
        if (queryUser != null && !Objects.equals(queryUser.nickname, oldNickname)) {
            return "昵称已存在";
        }
        UserDao.setUser(user);
        return null;
    }

    public static String updatePassword(User user, String oldPassword) throws Exception {
        if (!UserCheckService.checkPasswordLengthOk(user.password)) {
            return "密码长度不合法";
        }
        user.password = ToolUtils.getSha256Str(user.password);
        if (user.password.equals(oldPassword)) {
            return "新密码和原密码一致";
        }
        UserDao.setUser(user);
        return null;
    }
}
