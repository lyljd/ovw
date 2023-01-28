package dao;

import model.VideoLikes;

import java.sql.SQLException;
import java.util.ArrayList;

public abstract class VideoLikesDao {
    public static ArrayList<VideoLikes> getLikes(int uid) throws Exception {
        return BaseDao.query(VideoLikes.class, "select * from video_likes where uid = ?", uid);
    }

    public static VideoLikes getLike(int uid, int vid) throws Exception {
        ArrayList<VideoLikes> query = BaseDao.query(VideoLikes.class, "select * from video_likes where uid = ? and vid = ?", uid, vid);
        if (query.isEmpty()) {
            return null;
        } else {
            return query.get(0);
        }
    }

    public static void addLike(int uid, int vid) throws SQLException {
        BaseDao.update("insert into video_likes values(?, ?)", vid, uid);
    }

    public static void deleteLike(int uid, int vid) throws SQLException {
        BaseDao.update("delete from video_likes where uid = ? and vid = ?", uid, vid);
    }

    public static void deleteLikes(int vid) throws SQLException {
        BaseDao.update("delete from video_likes where vid = ?", vid);
    }
}
