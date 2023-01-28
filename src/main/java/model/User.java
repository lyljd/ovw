package model;

public class User {
    public int id;
    public String username;
    public String nickname;
    public String password;
    public String signature;
    public String avatar;

    //该构造函数专门写给用户注册时快速使用
    public User(String un, String nn, String pw) {
        username = un;
        nickname = nn;
        password = pw;
    }

    public User() {
    } //model层的orm类如果有除默认外的构造函数，那必须显式定义默认的构造函数，否则在query时反射会出问题。

    @Override
    public String toString() {
        return "User [" + id + ", " + username + ", " + nickname + ", " + password + ", " + signature + ", " + avatar + "]";
    }
}
