package controller;

import dao.UserDao;
import model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public abstract class CommonService {
    public static User checkLogin(HttpServletRequest req) throws Exception {
        String username = null;
        String password = null;
        Cookie[] cookies = req.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("username")) {
                username = cookie.getValue();
            } else if (cookie.getName().equals("password")) {
                password = cookie.getValue();
            }
        }

        User user = UserDao.getUserByUsername(username);
        if (user == null || !user.password.equals(password)) {
            return null;
        }
        return user;
    }
}
