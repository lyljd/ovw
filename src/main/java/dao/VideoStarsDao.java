package dao;

import model.VideoLikes;
import model.VideoStars;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class VideoStarsDao {
    public static ArrayList<VideoStars> getStars(int uid) throws Exception {
        return BaseDao.query(VideoStars.class, "select * from video_stars where uid = ?", uid);
    }

    public static VideoStars getStar(int uid, int vid) throws Exception {
        ArrayList<VideoStars> query = BaseDao.query(VideoStars.class, "select * from video_stars where uid = ? and vid = ?", uid, vid);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static void addStar(int uid, int vid) throws SQLException {
        BaseDao.update("insert into video_stars values(?, ?)", vid, uid);
    }

    public static void deleteStar(int uid, int vid) throws SQLException {
        BaseDao.update("delete from video_stars where uid = ? and vid = ?", uid, vid);
    }

    public static void deleteStars(int vid) throws SQLException {
        BaseDao.update("delete from video_stars where vid = ?", vid);
    }
}
