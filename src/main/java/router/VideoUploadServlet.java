package router;

import dao.VideoDao;
import model.*;
import org.apache.commons.fileupload.FileItem;
import controller.CommonService;
import controller.VideoCheckService;
import utils.HttpUtils;
import utils.JdbcUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@WebServlet("/video/upload")
public class VideoUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        List<FileItem> list = HttpUtils.getMultipartFormDataList(req);
        if (list == null) {
            FailResp.ret(resp, 400, "请求体有误");
            return;
        }

        Video v = new Video();
        FileItem cover = null;
        FileItem video = null;
        for (FileItem item : list) {
            switch (item.getFieldName()) {
                case "title":
                    if (!item.isFormField()) {
                        FailResp.ret(resp, 400, "title不是字符串");
                        return;
                    }
                    v.title = item.getString("utf-8");
                    break;
                case "info":
                    if (!item.isFormField()) {
                        FailResp.ret(resp, 400, "info不是字符串");
                        return;
                    }
                    v.info = item.getString("utf-8");
                    break;
                case "cover":
                    if (item.isFormField()) {
                        FailResp.ret(resp, 400, "cover不是文件");
                        return;
                    }
                    cover = item;
                    break;
                case "video":
                    if (item.isFormField()) {
                        FailResp.ret(resp, 400, "video不是文件");
                        return;
                    }
                    video = item;
                    break;
            }
        }

        if (v.title == null) {
            FailResp.ret(resp, 400, "请求体未解析到title字段或title字段值为空");
            return;
        }
        if (!VideoCheckService.checkTitleLengthOk(v.title)) {
            FailResp.ret(resp, 400, "标题长度不合法");
            return;
        }
        if (!VideoCheckService.checkInfoLengthOk(v.info)) {
            FailResp.ret(resp, 400, "简介长度不合法");
            return;
        }

        if (cover == null) {
            FailResp.ret(resp, 400, "请求体未解析到cover字段或cover字段值为空");
            return;
        }
        if (video == null) {
            FailResp.ret(resp, 400, "请求体未解析到video字段或video字段值为空");
            return;
        }

        String cn = cover.getName();
        int ci = cn.lastIndexOf(".");
        if (ci == -1) {
            FailResp.ret(resp, 400, "封面文件后缀名有误");
            return;
        }
        String vn = video.getName();
        int vi = vn.lastIndexOf(".");
        if (vi == -1) {
            FailResp.ret(resp, 400, "视频文件后缀名有误");
            return;
        }

        String ct = cn.substring(ci);
        if (!ct.equals(".jpg") && !ct.equals(".jpeg") && !ct.equals(".png") && !ct.equals(".gif")) {
            FailResp.ret(resp, 400, "封面文件类型 " + ct + " 不在允许范围内");
            return;
        }
        String vt = vn.substring(vi);
        if (!vt.equals(".mp4")) {
            FailResp.ret(resp, 400, "视频文件类型 " + vt + " 不在允许范围内");
            return;
        }

        Connection conn = JdbcUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            final String folderPath = getServletContext().getRealPath("/") + "\\resource\\";
            v.cover = UUID.randomUUID() + ct;
            v.video = UUID.randomUUID() + vt;
            v.uid = user.id;
            VideoDao.addVideo(v);
            cover.write(new File(folderPath + "cover\\" + v.cover));
            video.write(new File(folderPath + "video\\" + v.video));

            conn.commit();

            v.id = VideoDao.getVideoMaxId();
            SuccResp.ret(resp, v);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                //数据回滚失败，后序容易造成数据库数据错乱，属于严重错误，不过出现这种错误时一般服务器都该崩了。
                FailResp.ret(resp, 500, "服务器内出现严重错误1，请联系网站管理员解决");
                return;
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                //mysql无法自动保存了，属于严重错误，不过出现这种错误时一般服务器都该崩了。
                FailResp.ret(resp, 500, "服务器内出现严重错误2，请联系网站管理员解决");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        req.setAttribute("page", "投稿");
        req.setAttribute("url", "/video/upload");
        req.getRequestDispatcher("/WEB-INF/upload.jsp").forward(req, resp);
    }
}
