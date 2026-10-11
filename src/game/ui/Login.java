package game.ui;

import game.domain.User;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Login {
    public void start() {

        System.out.println("游戏的登录注册页面打开了~");

        ArrayList<User> list = new ArrayList<>();

        while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("    🎮 欢迎来到文字格斗游戏 🎮   ");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("请选择操作：1登录 2注册 3退出");

            Scanner sc = new Scanner(System.in);
            String choose = sc.next();

            switch (choose) {
                case "1" -> login(list);
                case "2" -> register(list);
                case "3" -> {
                    System.out.println("用户选择了退出操作");
                    System.exit(0);
                }
                default -> System.out.println("输入有误，请重新选择");
            }
        }

    }

    // 登录方法
    public void login(ArrayList<User> list) {
        System.out.println("用户选择了登录操作");

        //判断用户名是否存在
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username = sc.next();

        //不存在，提示未注册
        if(!isUsernameExists(list, username)) {
            System.out.println("用户名" + username + "未注册，请先注册");
            return;
        }

        //存在：禁用 提示联系客服
        int index = findIndex(list, username);
        if(!list.get(index).isStatus()){
            System.out.println("用户" + username + "已禁用，请联系客服XXX-XXXXXX");
            return;
        }

        //输入密码和验证码


        //判断密码是否正确
        String rightPassword = list.get(index).getPassword();
        for (int i = 0; i < 3; i++) {
            System.out.println("请输入密码：");
            String password = sc.next();

            //每次验证密码都要输入验证码
            while (true) {
                //先生成验证码
                String rightCode = getCode();
                System.out.println("验证码是：" + rightCode);

                System.out.println("请输入验证码：");
                String code = sc.next();

                if(rightCode.equalsIgnoreCase(code)){
                    System.out.println("验证码输入正确！");
                    break;
                } else {
                    System.out.println("验证码输入错误");
                    continue;
                }
            }

            if(rightPassword.equals(password)){
                System.out.println("登录成功，游戏启动");
                FightingGame fg = new FightingGame();
                fg.gameStart(username);
                break;
            }else{
                System.out.println("登录失败，密码输入错误");
                if(i == 2){
                    System.out.println("当前账号"+username+"已锁定，请联系客服XXX-XXXXXX");
                    list.get(index).setStatus(false);
                    return;
                }else{
                    System.out.println("密码错误，还剩下" + (2-i) + "次机会");
                }
            }
        }
    }

    // 注册方法
    public void register(ArrayList<User> list) {
        System.out.println("用户选择了注册操作");
        //创建用户对象
        User user = new User();

        //键盘输入用户名和密码
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("请输入用户名：");
            String username = sc.next();
            //用户名唯一 长度3-16位 包含数字和字母，不能纯数字
            if(!checkLen(3, 16, username)) {
                System.out.println("用户名长度不符合要求，必须在3-16位之间");
                continue;
            }
            if(!checkUsername(username)){
                System.out.println("用户名只能由数字和字母，不能纯数字");
                continue;
            }
            if(isUsernameExists(list, username)) {
                System.out.println("用户名已存在，请重新输入");
                continue;
            }
            user.setUsername(username);
            break;
        }

        while (true) {
            System.out.println("请输入密码：");
            String password = sc.next();
            System.out.println("请再次输入密码：");
            String password2 = sc.next();

            //长度3-8位
            if(!checkLen(3, 8, password)) {
                System.out.println("密码长度不符合要求，必须在3-8位之间");
                continue;
            }
            //只能是数字加字母组合
            if(!checkPassword(password)){
                System.out.println("密码只能由数字和字母，不能有其他字符");
                continue;
            }
            //两次输入的密码一致
            if(!password.equals(password2)) {
                System.out.println("两次输入的密码不一致，请重新输入");
                continue;
            }
            user.setPassword(password);
            break;
        }

        //把User对象放到ArrayList集合中
        list.add(user);

        //提示成功
        System.out.println("用户" + user.getUsername() + "注册成功！");

    }

    //查找用户在集合中的索引
    public int findIndex(ArrayList<User> list, String username){
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getUsername().equals(username)) {
                return i;
            }
        }
        return -1;
    }
    // 检查长度是否符合要求
    public boolean checkLen(int minLen, int maxLen, String str){
        return str.length() >= minLen && str.length() <= maxLen;
    }

    // 检查用户名是否符合要求
    public boolean checkUsername(String username) {
        int[] count = getCount(username);
        return count[0] > 0 && count[1] >= 0 && count[2] == 0;
    }

    // 检查密码是否符合要求
    public boolean checkPassword(String password) {
        int[] count = getCount(password);
        return count[0] > 0 && count[1] > 0 && count[2] == 0;
    }

    //用户名在集合中是否存在
    public boolean isUsernameExists(ArrayList<User> list, String username) {
        for (User user : list) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    //统计字符串中字符、数字、其他字符的个数
    public int[] getCount(String UserInfo){
        int charCount = 0;
        int numCount = 0;
        int otherCount = 0;
        for(int i = 0; i < UserInfo.length(); i++) {
            char c = UserInfo.charAt(i);
            if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
                charCount++;
            } else if(c >= '0' && c <= '9') {
                numCount++;
            } else {
                otherCount++;
            }
        }
        return new int[]{charCount, numCount, otherCount};
    }

    //生成验证码
    public static String getCode(){
        //把所有大小写字母都放在一起
        ArrayList<Character> list = new ArrayList<>();
        for(int i = 0; i < 26; i++) {
            list.add((char)('a' + i));
            list.add((char)('A' + i));
        }

        //随机获4个字符
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for(int i = 0; i < 4; i++){
            int index = r.nextInt(list.size());
            sb.append(list.get(index));
        }

        //生成一位随机数字
        sb.append(r.nextInt(10));

        //数字的位子是任意
        char[] arr = sb.toString().toCharArray();
        int index = r.nextInt(arr.length);
        char temp = arr[index];
        arr[index] = arr[arr.length-1];
        arr[arr.length-1] = temp;

        //变回字符串
        String code = new String(arr);

        return code;
    }
}
