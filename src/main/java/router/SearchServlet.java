package router;

import dao.UserDao;
import dao.VideoDao;
import model.Video;
import model.VideoView;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Objects;

@WebServlet("/videos/search")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain; charset=utf-8");

        try {
            String key = req.getParameter("key");
            ArrayList<Video> videos = VideoDao.searchVideos(key);
            ArrayList<VideoView> vvs = new ArrayList<>();
            for (Video v : videos) {
                vvs.add(new VideoView(v, Objects.requireNonNull(UserDao.getUserById(v.uid))));
            }
            //SuccResp.ret(resp, vvs);
            req.setAttribute("videos", vvs);
            req.setAttribute("page", "搜索 - " + key);
            req.setAttribute("key", key);
            req.getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
