package model;

public class UserView {
    public int id;
    public String username;
    public String nickname;
    public String password;
    public String signature;
    public String avatar;

    public UserView(User u) {
        id = u.id;
        username = u.username;
        nickname = u.nickname;
        password = u.password;
        signature = u.signature;
        if (u.avatar != null) {
            avatar = "http://localhost:8080/resource/avatar/" + u.avatar;
        }
    }

    @Override
    public String toString() {
        return "User [" + id + ", " + username + ", " + nickname + ", " + password + ", " + signature + ", " + avatar + "]";
    }
}
