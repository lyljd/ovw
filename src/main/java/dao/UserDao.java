package dao;

import model.User;
import model.UserView;
import utils.ToolUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class UserDao {
    public static User getUserById(int id) throws Exception {
        ArrayList<User> query = BaseDao.query(User.class, "select * from user where id = ?", id);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static User getUserByUsername(String username) throws Exception {
        ArrayList<User> query = BaseDao.query(User.class, "select * from user where username = ?", username);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static User getUserByNickname(String nickname) throws Exception {
        ArrayList<User> query = BaseDao.query(User.class, "select * from user where nickname = ?", nickname);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static void addUser(User u) throws SQLException {
        BaseDao.update("insert into user values(?, ?, ?, ?, ?, ?)", null, u.username, u.nickname, u.password, u.signature, u.avatar);
    }

    public static void setUser(User u) throws SQLException {
        BaseDao.update("update user set nickname = ?, password = ?, signature = ?, avatar = ? where id = ?", u.nickname, u.password, u.signature, u.avatar, u.id);
    }

    public static void setCookie(HttpServletRequest req, HttpServletResponse resp, UserView user) throws NoSuchAlgorithmException {
        //设置cookie
        if (user.avatar != null) {
            Cookie c4 = new Cookie("avatar", user.avatar);
            c4.setMaxAge(2592000);
            c4.setPath("/");
            resp.addCookie(c4);
        }
        Cookie c1 = new Cookie("username", user.username);
        Cookie c2 = new Cookie("password", user.password);
        Cookie c3 = new Cookie("nickname", user.nickname);

        c1.setMaxAge(2592000); //30天
        c2.setMaxAge(2592000);
        c3.setMaxAge(2592000);

        c1.setPath("/");
        c2.setPath("/");
        c3.setPath("/");

        resp.addCookie(c1);
        resp.addCookie(c2);
        resp.addCookie(c3);
    }
}
