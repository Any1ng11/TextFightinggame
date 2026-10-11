package game.domain;

public class EnemyCharacter extends Character {
    public String skill; //技能只有一个
    public boolean defending; //当前人物是否有减伤状态

    public EnemyCharacter(){}

    public EnemyCharacter(String name, int HP, int attack, int defense, int speed, String skill){
        super(name, HP, attack, defense, speed);
        this.skill = skill;
    }

    @Override
    public void takeDamage(int damage){
        if(defending){
            damage = damage / 2 > 1 ? damage / 2 : 1;
            defending = false;
            System.out.println(name + " 的防御姿态减免了一半伤害！");
        }
        super.takeDamage(damage);
    }
}
