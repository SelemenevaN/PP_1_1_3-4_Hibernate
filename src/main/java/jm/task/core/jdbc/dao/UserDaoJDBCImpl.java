package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final Connection connection;

    static {
        connection = Util.getConnection();
    }

    public UserDaoJDBCImpl() {

    }

    @Override
    public void createUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("create table if not exists mydbtest.usertable"
                    + "(id bigint primary key auto_increment, name varchar(40), lastname varchar(40), age tinyint)");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error upon creation a table");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void dropUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("drop table if exists mydbtest.usertable");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        String sql = "INSERT INTO mydbtest.usertable(name, lastname, age) VALUES(?,?,?)";
        try (PreparedStatement prStatement = connection.prepareStatement(sql)) {
            prStatement.setString(1, name);
            prStatement.setString(2, lastName);
            prStatement.setByte(3, age);
            prStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void removeUserById(long id) {
        try (PreparedStatement prStatement = connection.prepareStatement("DELETE FROM mydbtest.usertable where id")) {
            prStatement.executeUpdate("DELETE FROM mydbtest.usertable where id");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public List<User> getAllUsers() {

        List<User> allUser = new ArrayList<>();
        String sql = "SELECT id, name, lastName, age from mydbtest.usertable";

        try (PreparedStatement prStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = prStatement.executeQuery(sql);

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setName(resultSet.getString("name"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getByte("age"));
                allUser.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return allUser;
    }

    @Override
    public void cleanUsersTable() {
        String sql = "TRUNCATE TABLE mydbtest.usertable";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
    }
}

