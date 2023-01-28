package router;

import com.alibaba.fastjson2.JSONObject;
import controller.CommonService;
import controller.UserService;
import dao.UserDao;
import model.FailResp;
import model.SuccResp;
import model.User;
import model.UserView;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user/update/password")
public class UserUpdatePasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=utf-8");

        User user;
        try {
            user = CommonService.checkLogin(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            FailResp.ret(resp, 401, "请登录");
            return;
        }

        JSONObject obj = HttpUtils.getJsonObj(req);

        String password = obj.getString("password");
        String password2 = obj.getString("password2");

        if (!password2.equals(password)) {
            FailResp.ret(resp, 400, "两次输入的密码不一致");
            return;
        }

        String oldPassword = user.password;
        user.password = password;

        try {
            String error = UserService.updatePassword(user, oldPassword);
            if (error != null) {
                FailResp.ret(resp, 400, error);
                return;
            }
            UserView userView = new UserView(user);
            UserDao.setCookie(req, resp, userView);
            SuccResp.ret(resp, userView);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=utf-8");

        User user;
        try {
            user = CommonService.checkLogin(req);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            req.setAttribute("status", 401);
            req.setAttribute("msg", "请登录");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("page", "修改密码");
        req.setAttribute("url", "/user/update/password");
        req.getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
    }
}
