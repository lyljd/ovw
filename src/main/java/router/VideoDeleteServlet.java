package router;

import com.alibaba.fastjson2.JSONObject;
import controller.CommonService;
import dao.VideoDao;
import dao.VideoLikesDao;
import dao.VideoStarsDao;
import model.FailResp;
import model.User;
import model.Video;
import utils.HttpUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/video/delete")
public class VideoDeleteServlet extends HttpServlet {
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

        int id;
        try {
            JSONObject obj = HttpUtils.getJsonObj(req);
            id = Integer.parseInt(obj.getString("id"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e) {
            FailResp.ret(resp, 400, "id有误");
            return;
        }

        Video video;
        try {
            video = VideoDao.getVideoById(id);
            if(video == null) {
                FailResp.ret(resp, 404, "视频不存在");
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (video.uid != user.id) {
            FailResp.ret(resp, 403, "该视频不属于你");
            return;
        }

        try {
            VideoDao.deleteVideo(id);
            VideoLikesDao.deleteLikes(id);
            VideoStarsDao.deleteStars(id);

            final String folderPath = getServletContext().getRealPath("/") + "\\resource\\";
            if (!new File(folderPath + "cover\\" + video.cover).delete()) {
                System.out.println("文件\"" + folderPath + "cover\\" + video.cover + "\"删除失败，请手动删除");
            }
            if (!new File(folderPath + "video\\" + video.video).delete()) {
                System.out.println("文件\"" + folderPath + "video\\" + video.video + "\"删除失败，请手动删除");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
