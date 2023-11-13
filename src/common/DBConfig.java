package common;

public interface DBConfig {
    String getJdbcUrl();
    String getUsername();
    String getUserPw();

    void localServerTest(String url, String user, String password);
}
