package common;

public interface DBConfig {
    String getJdbcUrl();
    String getUsername();
    String getUserPw();


    void createTest(String url, String user, String password);


}
