package router;

import dao.UserDao;
import dao.VideoDao;
import dao.VideoLikesDao;
import dao.VideoStarsDao;
import model.*;
import controller.CommonService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@WebServlet({"/me", "/me/upload", "/me/like", "/me/star"})
public class UserMeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain; charset=utf-8");

        try {
            User user = CommonService.checkLogin(req);
            if (user == null) {
                //FailResp.ret(resp, 401, null);
                req.setAttribute("status", 401);
                req.setAttribute("msg", "请登录");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                return;
            }
            String path = String.valueOf(req.getRequestURI());
            ArrayList<VideoView> vvs = new ArrayList<>();
            ArrayList<Video> videos = new ArrayList<>();
            switch (path) {
                case "/me":
                    resp.sendRedirect("/me/upload");
                    return;
                case "/me/upload":
                    videos = VideoDao.getMyVideos(user.id);
                    for (Video v : videos) {
                        vvs.add(new VideoView(v, Objects.requireNonNull(UserDao.getUserById(v.uid))));
                    }
                    req.setAttribute("videos", vvs);
                    req.setAttribute("page", "我的投稿");
                    break;
                case "/me/like":
                    ArrayList<VideoLikes> videoLikes = VideoLikesDao.getLikes(user.id);
                    for (VideoLikes vl : videoLikes) {
                        videos.add(VideoDao.getVideoById(vl.vid));
                    }
                    for (Video v : videos) {
                        vvs.add(new VideoView(v, Objects.requireNonNull(UserDao.getUserById(v.uid))));
                    }
                    req.setAttribute("videos", vvs);
                    req.setAttribute("page", "我的点赞");
                    break;
                case "/me/star":
                    ArrayList<VideoStars> videoStars = VideoStarsDao.getStars(user.id);
                    for (VideoStars vs : videoStars) {
                        videos.add(VideoDao.getVideoById(vs.vid));
                    }
                    for (Video v : videos) {
                        vvs.add(new VideoView(v, Objects.requireNonNull(UserDao.getUserById(v.uid))));
                    }
                    req.setAttribute("videos", vvs);
                    req.setAttribute("page", "我的收藏");
                    break;
                default:
                    req.setAttribute("status", 400);
                    req.setAttribute("msg", "非法访问");
                    req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                    return;
            }
            //SuccResp.ret(resp, user);
            req.setAttribute("user", new UserView(user));
            req.setAttribute("who", "我");
            req.getRequestDispatcher("/WEB-INF/user.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
