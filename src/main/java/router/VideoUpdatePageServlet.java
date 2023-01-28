package router;

import controller.CommonService;
import dao.VideoDao;
import model.User;
import model.Video;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/video/update/*")
public class VideoUpdatePageServlet extends HttpServlet {
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

        int id;
        try {
            id = Integer.parseInt(req.getPathInfo().substring(1));
        } catch (NumberFormatException e) {
            req.setAttribute("status", 400);
            req.setAttribute("msg", "id有误");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        }

        Video video;
        try {
            video = VideoDao.getVideoById(id);
            if (video == null) {
                req.setAttribute("status", 404);
                req.setAttribute("msg", "视频不存在");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (video.uid != user.id) {
            req.setAttribute("status", 403);
            req.setAttribute("msg", "无权限");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("page", "修改");
        req.setAttribute("url", "/video/update");
        req.setAttribute("title", video.title);
        req.setAttribute("info", video.info);
        req.setAttribute("id", video.id);
        req.getRequestDispatcher("/WEB-INF/upload.jsp").forward(req, resp);
    }
}
