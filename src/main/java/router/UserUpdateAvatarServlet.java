package router;

import dao.UserDao;
import model.FailResp;
import model.SuccResp;
import model.User;
import model.UserView;
import org.apache.commons.fileupload.FileItem;
import controller.CommonService;
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

@WebServlet("/user/update/avatar")
public class UserUpdateAvatarServlet extends HttpServlet {
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

        FileItem file = null;
        for (FileItem f : list) {
            if (f.getFieldName().equals("avatar")) {
                file = f;
                break;
            }
        }
        if (file == null) {
            FailResp.ret(resp, 400, "请求体未解析到avatar字段或avatar字段值为空");
            return;
        }

        if (file.isFormField()) {
            FailResp.ret(resp, 400, "avatar不是文件");
            return;
        }

        String fn = file.getName();
        int i = fn.lastIndexOf(".");
        if (i == -1) {
            FailResp.ret(resp, 400, "头像文件后缀名有误");
            return;
        }

        String ft = fn.substring(i);
        if (!ft.equals(".jpg") && !ft.equals(".jpeg") && !ft.equals(".png") && !ft.equals(".gif")) {
            FailResp.ret(resp, 400, "头像文件类型 " + ft + " 不在允许范围内");
            return;
        }

        Connection conn = JdbcUtils.getConnection();
        try {
            conn.setAutoCommit(false);

            final String folderPath = getServletContext().getRealPath("/") + "\\resource\\avatar\\";
            if (user.avatar != null) {
                if (!new File(folderPath + user.avatar).delete()) {
                    System.out.println("文件\"" + folderPath + user.avatar + "\"删除失败，请手动删除");
                }
            }
            user.avatar = UUID.randomUUID() + ft;
            UserDao.setUser(user);
            file.write(new File(folderPath + user.avatar));

            conn.commit();

            UserView userView = new UserView(user);
            UserDao.setCookie(req, resp, userView);
            SuccResp.ret(resp, userView);
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

        req.setAttribute("page", "修改头像");
        req.setAttribute("url", "/user/update/avatar");
        req.getRequestDispatcher("/WEB-INF/update.jsp").forward(req, resp);
    }
}
