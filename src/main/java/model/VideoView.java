package model;

public class VideoView {
    public int id;
    public String title;
    public String info;
    public String time;
    public String cover;
    public String video;
    public int views;
    public int likes;
    public int stars;
    public int uid;
    public String nickname;
    public String signature;
    public String avatar;

    public VideoView(Video v, User u) {
        id = v.id;
        title = v.title;
        info = v.info;
        time = v.time;
        cover = "http://localhost:8080/resource/cover/" + v.cover;
        video = "http://localhost:8080/resource/video/" + v.video;
        views = v.views;
        likes = v.likes;
        stars = v.stars;
        uid = v.uid;
        nickname = u.nickname;
        signature = u.signature;
        if (u.avatar != null) {
            avatar = "http://localhost:8080/resource/avatar/" + u.avatar;
        }
    }

    @Override
    public String toString() {
        return "VideoView [" + id + ", " + title + ", " + info + ", " + time + ", " + cover + ", " + video + ", " + views + ", " + likes + ", " + stars + ", " + uid + ", " + nickname + ", " + signature + ", " + avatar + "]";
    }
}
