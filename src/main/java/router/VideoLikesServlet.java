package router;

import com.alibaba.fastjson2.JSONObject;
import controller.CommonService;
import controller.VideoCheckService;
import dao.UserDao;
import dao.VideoDao;
import dao.VideoLikesDao;
import model.*;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/video/like")
public class VideoLikesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //读id并验证id合法性
        resp.setContentType("text/plain; charset=utf-8");

        User user = null;
        try {
            user = CommonService.checkLogin(req);
            if (user == null) {
                FailResp.ret(resp, 401, "请登录");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject obj = HttpUtils.getJsonObj(req);

        String sVid = obj.getString("vid");

        int vid;
        try {
            vid = Integer.parseInt(sVid);
        } catch (NumberFormatException e) {
            FailResp.ret(resp, 400, "vid有误");
            return;
        }

        Video video;
        try {
            video = VideoDao.getVideoById(vid);
            if (video == null) {
                FailResp.ret(resp, 404, "视频不存在");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //判断是否点过赞。点过，delete；没点过，add
        boolean isLike;
        try {
            if (VideoCheckService.checkLikes(user.id, vid)) {
                VideoLikesDao.deleteLike(user.id, vid);
                video.likes--;
                isLike = false;
                VideoDao.setVideo(video);
            } else {
                VideoLikesDao.addLike(user.id, vid);
                video.likes++;
                isLike = true;
                VideoDao.setVideo(video);
            }
            SuccResp.ret(resp, new LikeOrStarView(isLike, video.likes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
