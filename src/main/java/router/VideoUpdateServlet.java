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

@WebServlet("/video/update")
public class VideoUpdateServlet extends HttpServlet {
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
        Video vv = null;
        FileItem cover = null;
        FileItem video = null;
        boolean hasId = false;
        for (FileItem item : list) {
            switch (item.getFieldName()) {
                case "id":
                    hasId = true;
                    if (!item.isFormField()) {
                        FailResp.ret(resp, 400, "id不是字符串");
                        return;
                    }
                    try {
                        v.id = Integer.parseInt(item.getString("utf-8"));
                        vv = VideoDao.getVideoById(v.id);
                        if (vv == null) {
                            FailResp.ret(resp, 404, "视频不存在");
                            return;
                        }
                        if (vv.uid != user.id) {
                            FailResp.ret(resp, 403, "该视频不属于你");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        FailResp.ret(resp, 400, "id有误");
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
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

        if (!hasId) {
            FailResp.ret(resp, 400, "请求体未解析到id");
            return;
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

        String ct = null;
        if (cover != null) {
            String cn = cover.getName();
            int ci = cn.lastIndexOf(".");
            if (ci == -1) {
                FailResp.ret(resp, 400, "封面文件后缀名有误");
                return;
            }

            ct = cn.substring(ci);
            if (!ct.equals(".jpg") && !ct.equals(".jpeg") && !ct.equals(".png") && !ct.equals(".gif")) {
                FailResp.ret(resp, 400, "封面文件类型 " + ct + " 不在允许范围内");
                return;
            }
        }

        String vt = null;
        if (video != null) {
            String vn = video.getName();
            int vi = vn.lastIndexOf(".");
            if (vi == -1) {
                FailResp.ret(resp, 400, "视频文件后缀名有误");
                return;
            }

            vt = vn.substring(vi);
            if (!vt.equals(".mp4")) {
                FailResp.ret(resp, 400, "视频文件类型 " + vt + " 不在允许范围内");
                return;
            }
        }

        Connection conn = JdbcUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            final String folderPath = getServletContext().getRealPath("/") + "\\resource\\";
            if (cover != null) {
                if (!new File(folderPath + "cover\\" + vv.cover).delete()) {
                    System.out.println("文件\"" + folderPath + "cover\\" + v.cover + "\"删除失败，请手动删除");
                }
                vv.cover = UUID.randomUUID() + ct;
            }
            if (video != null) {
                if (!new File(folderPath + "video\\" + vv.video).delete()) {
                    System.out.println("文件\"" + folderPath + "video\\" + v.video + "\"删除失败，请手动删除");
                }
                vv.video = UUID.randomUUID() + vt;
            }
            vv.title = v.title;
            vv.info = v.info;
            VideoDao.setVideo(vv);
            if (cover != null) {
                cover.write(new File(folderPath + "cover\\" + vv.cover));
            }
            if (video != null) {
                video.write(new File(folderPath + "video\\" + vv.video));
            }

            conn.commit();

            SuccResp.ret(resp, vv);
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
}
