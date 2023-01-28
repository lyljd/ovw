package router;

import com.alibaba.fastjson2.JSONObject;
import dao.UserDao;
import model.FailResp;
import model.SuccResp;
import model.User;
import controller.UserService;
import model.UserView;
import utils.HttpUtils;
import utils.ToolUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=utf-8");

        JSONObject obj = HttpUtils.getJsonObj(req);

        String username = obj.getString("username");
        String password = obj.getString("password");

        try {
            User u = UserService.login(username, password);
            UserView user = null;
            if (u != null) {
                user = new UserView(u);
            }
            if (user == null) {
                FailResp.ret(resp, 400, "账号或密码错误");
            } else {
                UserDao.setCookie(req, resp, user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
    }
}
