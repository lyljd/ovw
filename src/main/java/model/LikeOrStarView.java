package model;

public class LikeOrStarView {
    public boolean status; // true：点赞，false：取消点赞
    public int num; // 操作后的最新对应数量

    public LikeOrStarView(boolean sta, int n) {
        status = sta;
        num = n;
    }
}
