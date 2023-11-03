package db;

import common.DBConfig;

public class DBConnection implements DBConfig {
    private String url;
    private String user;
    private String password;

    public void createTest(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public String getJdbcUrl() {
        return url;
    }

    @Override
    public String getUsername() {
        return user;
    }

    @Override
    public String getUserPw() {
        return password;
    }

}
