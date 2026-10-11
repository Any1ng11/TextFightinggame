package game.domain;

import java.util.Random;

public class User {
    private String id;
    private String username;
    private String password;
    private boolean status; //false 禁用 true 启用

    public User() {
        // 生成ID
        id = createID();
        status = true; // 默认启用
    }
    public User(String username, String password) {
        id = createID();
        this.username = username;
        this.password = password;
        status = true; // 默认启用
    }

    public String createID() {
        StringBuilder sb = new StringBuilder("Xi");

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int num = random.nextInt(10);
            sb.append(num);
        }
        return sb.toString();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
}
