package router;

import com.alibaba.fastjson2.JSONObject;
import controller.CommonService;
import controller.UserService;
import dao.UserDao;
import model.*;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user/update/info")
public class UserUpdateInfoServlet extends HttpServlet {
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

        String oldNickname = user.nickname;
        user.nickname = obj.getString("nickname");
        user.signature = obj.getString("signature");

        try {
            String error = UserService.updateInfo(user, oldNickname);
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

        req.setAttribute("page", "修改信息");
        req.setAttribute("url", "/user/update/info");
        req.setAttribute("nickname", user.nickname);
        req.setAttribute("signature", user.signature);
        req.getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
    }
}
