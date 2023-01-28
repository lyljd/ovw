package dao;

import model.Video;
import model.VideoLikes;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class VideoDao {
    public static ArrayList<Video> getVideos() throws Exception {
        return BaseDao.query(Video.class, "select * from video");
    }

    public static Video getVideoById(int id) throws Exception {
        ArrayList<Video> query = BaseDao.query(Video.class, "select * from video where id = ?", id);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static void addVideo(Video v) throws SQLException {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        BaseDao.update("insert into video values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", null, v.title, v.info, time, v.cover, v.video, v.views, v.likes, v.stars, v.uid);
    }

    public static void setVideo(Video v) throws SQLException {
        BaseDao.update("update video set title = ?, info = ?, cover = ?, video = ?, views = ?, likes = ?, stars = ? where id = ?", v.title, v.info, v.cover, v.video, v.views, v.likes, v.stars, v.id);
    }

    public static void deleteVideo(int id) throws SQLException {
        BaseDao.update("delete from video where id = ?", id);
    }

    public static ArrayList<Video> searchVideos(String key) throws Exception {
        return BaseDao.query(Video.class, "select * from video where title like ?", "%" + key + "%");
    }

    public static ArrayList<Video> getMyVideos(int uid) throws Exception {
        return BaseDao.query(Video.class, "select * from video where uid = ?", uid);
    }

    public static int getVideoMaxId() throws Exception {
        ArrayList<Video> query = BaseDao.query(Video.class, "select * from video order by id desc");
        if (query.isEmpty()) {
            return 0;
        } else {
            return query.get(0).id;
        }
    }
}
