package game.domain;

import java.util.ArrayList;

public class HeroCharacter extends Character {
    public int MP; // 魔力值
    public int maxMP; // 最大魔力值
    public ArrayList<String> skillList; //技能列表


    public HeroCharacter(){
        super();
        skillList = new ArrayList<>();
    }
    public HeroCharacter(String name, int HP, int MP, int attack, int defense, int speed){
        super(name, HP, attack, defense, speed);
        this.MP = MP;
        this.maxMP = MP;
        skillList = new ArrayList<>();
    }

    public String showSkills(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < skillList.size(); i++){
            sb.append(skillList.get(i));
            if(i != skillList.size() - 1){
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    //恢复魔力值
    public void recoverMP(int amount){
        MP += amount;
        if (MP > maxMP){
            MP = maxMP;
        }
    }

    @Override
    public String show() {
        return name + "[当前生命：" + HP + ", 当前魔力：" + MP + ", 攻击：" + attack + ", 防御：" + defense + ", 速度：" + speed + "]";
    }
}
