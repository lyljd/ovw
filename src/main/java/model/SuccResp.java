package model;

import com.alibaba.fastjson2.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class SuccResp {
    public static void ret(HttpServletResponse resp, Object obj) throws IOException {
        resp.getWriter().write(JSON.toJSONString(obj));
    }
}
