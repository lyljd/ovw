package utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public abstract class HttpUtils {
    public static JSONObject getJsonObj(HttpServletRequest req) throws IOException {
        ServletInputStream inputStream = req.getInputStream();
        byte[] buf = new byte[1024];
        int len;
        StringBuilder res = new StringBuilder();
        while ((len = inputStream.read(buf)) != -1) {
            res.append(new String(buf, 0, len));
        }
        return JSON.parseObject(String.valueOf(res));
    }

    public static List<FileItem> getMultipartFormDataList(HttpServletRequest req) {
        if (!ServletFileUpload.isMultipartContent(req)) {
            return null;
        }

        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);

        List<FileItem> fis;
        try {
            fis = servletFileUpload.parseRequest(req);
        } catch (FileUploadException e) {
            return null;
        }

        return fis;
    }
}
