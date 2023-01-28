package router;

import controller.CommonService;
import dao.UserDao;
import dao.VideoDao;
import model.*;
import controller.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@WebServlet("/user/*")
public class UserOtherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/plain; charset=utf-8");

        int id;
        try {
            id = Integer.parseInt(req.getPathInfo().substring(1));
            User user = CommonService.checkLogin(req);
            if (user != null && user.id == id) {
                resp.sendRedirect("/me/upload");
                return;
            }
        } catch (NumberFormatException e) {
            //FailResp.ret(resp, 400, "uid有误");
            req.setAttribute("status", 400);
            req.setAttribute("msg", "id有误");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        User user;
        try {
            user = UserService.getOther(id);
            if (user == null) {
                //FailResp.ret(resp, 404, "用户不存在");
                req.setAttribute("status", 404);
                req.setAttribute("msg", "用户不存在");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            } else {
                //SuccResp.ret(resp, user);
                ArrayList<Video> videos = VideoDao.getMyVideos(user.id);
                ArrayList<VideoView> vvs = new ArrayList<>();
                for (Video v : videos) {
                    vvs.add(new VideoView(v, Objects.requireNonNull(UserDao.getUserById(v.uid))));
                }
                req.setAttribute("videos", vvs);
                req.setAttribute("page", "TA的投稿");
                req.setAttribute("user", new UserView(user));
                req.setAttribute("who", "TA");
                req.getRequestDispatcher("/WEB-INF/user.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
