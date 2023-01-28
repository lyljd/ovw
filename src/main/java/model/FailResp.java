package model;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FailResp {
    public String msg;

    private FailResp(String ms) {
        msg = ms;
    }

    public static void ret(HttpServletResponse resp, int status, String msg) throws IOException {
        if (status == 200) {
            System.out.println("[警告]你在FailResp.ret中返回了200状态码，已取消该次返回！");
            return;
        }
        if (msg == null) {
            switch (status) {
                case 400:
                    msg = "客服端未知错误";
                    break;
                case 401:
                    msg = "请登录";
                    break;
                case 403:
                    msg = "权限不足";
                    break;
                case 404:
                    msg = "资源不存在";
                    break;
                case 500:
                    msg = "服务器未知错误";
                    break;
                default:
                    msg = "未知错误";
            }
        }
        resp.setStatus(status);
        resp.getWriter().write(JSON.toJSONString(new FailResp(msg)));
    }
}
