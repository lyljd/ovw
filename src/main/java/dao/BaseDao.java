package dao;

import utils.JdbcUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;

public abstract class BaseDao {
    //增删改
    public static void update(String sql, Object... args) throws SQLException {
        PreparedStatement ps = JdbcUtils.getConnection().prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        ps.execute();
        ps.close();
    }

    //查
    public static <T> ArrayList<T> query(Class<T> clazz, String sql, Object... args) throws Exception {
        PreparedStatement ps = JdbcUtils.getConnection().prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        ResultSet rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        ArrayList<T> al = new ArrayList<>();
        while (rs.next()) {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (int i = 0; i < columnCount; i++) {
                Object columnValue = rs.getObject(i + 1);
                String columnLabel = rsmd.getColumnLabel(i + 1);
                Field declaredField = clazz.getDeclaredField(columnLabel);
                declaredField.setAccessible(true);
                declaredField.set(instance, columnValue);
            }
            al.add(instance);
        }
        ps.close();
        rs.close();
        return al;
    }
}
