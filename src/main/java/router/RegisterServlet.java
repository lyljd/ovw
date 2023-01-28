package router;

import com.alibaba.fastjson2.JSONObject;
import model.FailResp;
import model.User;
import controller.UserService;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=utf-8");

        JSONObject obj = HttpUtils.getJsonObj(req);

        String password = obj.getString("password");
        String password2 = obj.getString("password2");

        if (!password2.equals(password)) {
            FailResp.ret(resp, 400, "两次输入的密码不一致");
            return;
        }

        String username = obj.getString("username");
        String nickname = obj.getString("nickname");

        try {
            String error = UserService.register(new User(username, nickname, password2));
            if (error != null) {
                FailResp.ret(resp, 400, error);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/register.html").forward(req, resp);
    }
}
