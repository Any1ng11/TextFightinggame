package game.domain;

public class Character {
    public String name; //名字
    public int HP; //生命值
    public int maxHP; //最大生命值
    public int attack; //攻击力
    public int defense; //防御力
    public int speed; //速度
    public double critRate; // 暴击率
    public double critMultiplier; // 暴击倍率
    public StatusEffect statusHead; // 状态效果链表头

    public Character(){}

    public Character(String name, int HP, int attack, int defense, int speed) {
        this.name = name;
        this.HP = HP;
        this.maxHP = HP;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.critRate = 0.15;
        this.critMultiplier = 1.5;
        this.statusHead = null;
    }

    //判断人物是否存活
    public boolean isAlive(){
        return HP > 0;
    }

    //恢复生命值
    public void heal(int amount){
        HP += amount;
        if (HP > maxHP){
            HP = maxHP;
        }
    }

    //受到伤害
    public void takeDamage(int damage) {
        // 护盾优先吸收伤害
        int shield = StatusManager.getStatusValue(this, "护盾");
        if (shield > 0) {
            if (damage <= shield) {
                // 更新护盾值
                StatusEffect cur = statusHead;
                while (cur != null) {
                    if (cur.type.equals("护盾")) {
                        cur.value -= damage;
                        break;
                    }
                    cur = cur.next;
                }
                System.out.println("🛡 " + name + " 的护盾吸收了 " + damage + " 点伤害！");
                return;
            } else {
                damage -= shield;
                // 护盾破碎
                StatusEffect prev = null;
                StatusEffect cur = statusHead;
                while (cur != null) {
                    if (cur.type.equals("护盾")) {
                        if (prev == null) statusHead = cur.next;
                        else prev.next = cur.next;
                        break;
                    }
                    prev = cur;
                    cur = cur.next;
                }
                System.out.println("🛡 " + name + " 的护盾破碎！剩余伤害 " + damage + " 点");
            }
        }

        HP -= damage;
        if (HP < 0) HP = 0;
    }

    //展示人物的属性
    public String show(){
        return name + "[当前生命：" + HP + ", 攻击：" + attack + ", 防御：" + defense + ", 速度：" + speed + "]";
    }
}
