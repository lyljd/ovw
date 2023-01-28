package router;

import controller.CommonService;
import controller.VideoCheckService;
import dao.UserDao;
import dao.VideoDao;
import model.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@WebServlet("/video/*")
public class VideoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/plain; charset=utf-8");

        int id;
        try {
            id = Integer.parseInt(req.getPathInfo().substring(1));
        } catch (NumberFormatException e) {
            //FailResp.ret(resp, 400, "vid有误");
            req.setAttribute("status", 400);
            req.setAttribute("msg", "id有误");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        }

        try {
            Video video = VideoDao.getVideoById(id);
            if (video == null) {
                //FailResp.ret(resp, 404, "视频不存在");
                req.setAttribute("status", 404);
                req.setAttribute("msg", "视频不存在");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            } else {
                video.views++;
                VideoDao.setVideo(video);
                //SuccResp.ret(resp, new VideoView(video, Objects.requireNonNull(UserDao.getUserById(video.uid))));
                boolean islike = false;
                boolean isstar = false;
                User user = CommonService.checkLogin(req);
                if (user != null && VideoCheckService.checkLikes(user.id, video.id)) {
                    islike = true;
                }
                if (user != null && VideoCheckService.checkStars(user.id, video.id)) {
                    isstar = true;
                }
                req.setAttribute("islike", islike);
                req.setAttribute("isstar", isstar);
                req.setAttribute("video", new VideoView(video, Objects.requireNonNull(UserDao.getUserById(video.uid))));
                req.getRequestDispatcher("/WEB-INF/play.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
