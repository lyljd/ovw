package controller;

import dao.UserDao;

public abstract class UserCheckService {
    public static boolean checkIdExist(int id) throws Exception {
        return UserDao.getUserById(id) != null;
    }

    public static boolean checkUsernameExist(String username) throws Exception {
        return UserDao.getUserByUsername(username) != null;
    }

    public static boolean checkNicknameExist(String nickname) throws Exception {
        return UserDao.getUserByNickname(nickname) != null;
    }

    public static boolean checkUsernameLengthOk(String username) {
        return username.length() >= 3 && username.length() <= 10;
    }

    public static boolean checkNicknameLengthOk(String nickname) {
        return nickname.length() >= 3 && nickname.length() <= 10;
    }

    public static boolean checkPasswordLengthOk(String password) {
        return password.length() >= 6 && password.length() <= 20;
    }
}
