package model;

public class Video {
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

    @Override
    public String toString() {
        return "Video [" + id + ", " + title + ", " + info + ", " + time + ", " + cover + ", " + video + ", " + views + ", " + likes + ", " + stars + ", " + uid + "]";
    }
}
