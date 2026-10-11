import game.ui.FightingGame;
import game.ui.Login;

public class App {
   public static void main(String[] args) {
        /*Login l1 = new Login();
        l1.start();*/

        FightingGame fg = new FightingGame();
        fg.gameStart("zhangsan");
    }
}
