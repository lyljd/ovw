package controller;

import dao.VideoLikesDao;
import dao.VideoStarsDao;
import model.VideoLikes;
import model.VideoStars;

public abstract class VideoCheckService {
    public static boolean checkTitleLengthOk(String title) {
        return title.length() >= 2 && title.length() <= 30;
    }

    public static boolean checkInfoLengthOk(String info) {
        return info == null || info.length() <= 1000;
    }
    public static boolean checkLikes(int uid, int vid) throws Exception {
        VideoLikes res = VideoLikesDao.getLike(uid, vid);
        return res != null;
    }

    public static boolean checkStars(int uid, int vid) throws Exception {
        VideoStars res = VideoStarsDao.getStar(uid, vid);
        return res != null;
    }
}
