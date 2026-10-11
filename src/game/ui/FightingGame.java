package game.ui;

import game.domain.*;
import game.domain.Character;


import java.util.Random;
import java.util.Scanner;


public class FightingGame {

    // 二叉堆（优先队列）
    class ActionHeap{
        private Character[] heap;
        private int size;
        private static final int DEFAULT_CAPACITY = 10;

        public ActionHeap() {
            heap = new Character[DEFAULT_CAPACITY];
            size = 0;
        }

        public boolean isEmpty(){
            return size == 0;
        }

        public void insert(Character c){
            if(size == heap.length - 1){
                resize();
            }
            heap[++size] = c;
            siftUp(size);
        }

        public Character extractMax(){
            if(size==0) return null;
            Character max = heap[1];
            heap[1] = heap[size];
            heap[size] = null;
            size--;
            if(size > 0){
                siftDown(1);
            }
            return max;
        }
        private void siftUp(int index){
            while (index > 1 && getPriority(heap[index]) > getPriority(heap[index / 2])){
                swap(index, index / 2);
                index /= 2;
            }
        }
        private void siftDown(int index){
            while(index * 2 <=size){
                int left = index * 2;
                int right = index * 2 + 1;
                int larger = left;
                if (left <= size && getPriority(heap[right]) > getPriority(heap[left])) {
                    larger = right;
                }
                if (getPriority(heap[index]) >= getPriority(heap[larger])) {
                    break;
                }
                swap(index, larger);
                index = larger;
            }
        }

        private int getPriority(Character c){
            int base = c.speed;
            base += StatusManager.getStatusValue(c, "加速");
            return base;
        }

        private void swap(int i, int j){
            Character temp = heap[i];
            heap[i] = heap[j];
            heap[j] = temp;
        }

        private void resize(){
            Character[] newHeap = new Character[heap.length * 2];
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;
        }
    }


    //启动游戏
    public void gameStart(String username) {
        //显示游戏标题
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("     🎮" + username+ "欢迎来到文字格斗游戏 🎮   ");
        System.out.println("╚════════════════════════════════════════╝");

        //创建玩家角色（名字 + 属性分配）
        HeroCharacter player = createPlayerCharacter(username);
        System.out.println("角色创建成功！");
        System.out.println("\uD83C\uDF1F 初始属性为：" + player.show());
        System.out.println("\uD83C\uDF1F 拥有技能：" + player.showSkills());

        //创建敌人角色
        SeqList<EnemyCharacter> enemyList = new SeqList<>();
        enemyList.add(new EnemyCharacter("初级战士", 80, 15, 10, 8, "猛击"));
        enemyList.add(new EnemyCharacter("敏捷刺客", 60, 20, 5, 15, "快速攻击"));
        enemyList.add(new EnemyCharacter("重装坦克", 120, 10, 15, 5, "防御姿态"));
        enemyList.add(new EnemyCharacter("神秘法师", 70, 25, 8, 12, "火球术"));


        //准备战斗
        int count = 1; //记录当前与第几个敌人战斗
        int wins = 0; //记录胜利场次
        boolean escaped = false; //记录是否逃跑

        while (player.isAlive() && !escaped){
            //重置敌人的属性，敌人属性每场HP+10, ATK+3, DEF+2, SPD+1（敌人：越来越强）（第二场的时候）
            if(wins != 0){
                //获取每一个敌人信息
                for(int i = 0; i < enemyList.size(); i++){
                    EnemyCharacter c = enemyList.get(i);
                    c.maxHP += 10;
                    c.HP = c.maxHP;
                    c.attack += 3;
                    c.defense += 2;
                    c.speed += 1;
                    //重置敌人状态
                    c.defending = false;
                    //清楚上轮状态
                    c.statusHead = null;
                }
            }

            //随机选择一个敌人
            Random r = new Random();
            int index = r.nextInt(enemyList.size());
            EnemyCharacter enemy = enemyList.get(index);
            System.out.println(enemy.show());

            //开始战斗
            System.out.println("═══════════════════════════════════════");
            System.out.println("⚔\uFE0F 第 " + count + " 场战斗开始！对手: " + enemy.name);

            //第几回合
            int round = 1;
            while (player.isAlive() && enemy.isAlive() && !escaped){
                System.out.println("---------------------------------------");
                System.out.println("⚔\uFE0F第" + round + "回合开始！");
                //打印血条
                System.out.println(getHealthBar(player.name, player.HP, player.maxHP));
                System.out.println(getHealthBar(enemy.name, enemy.HP, enemy.maxHP));
                //打印状态
                System.out.println("你的状态:" + StatusManager.showStatus(player));
                System.out.println("敌人的状态:" + StatusManager.showStatus(enemy));

                //处理中毒扣血
                StatusManager.processTurnStart(player);
                StatusManager.processTurnStart(enemy);

                if(!player.isAlive() || !enemy.isAlive()) break;

                //构建行动堆
                //跳过眩晕阶段
                ActionHeap heap = new ActionHeap();
                if(!StatusManager.hasStatus(player, "眩晕")){
                    heap.insert(player);
                }else{
                    System.out.println("💫 " + player.name + " 处于眩晕状态，跳过行动！");
                }
                if(!StatusManager.hasStatus(enemy, "眩晕")){
                    heap.insert(enemy);
                }else{
                    System.out.println("💫 " + enemy.name + " 处于眩晕状态，跳过行动！");
                }

                if(heap.isEmpty()){
                    System.out.println("双方都无法行动，跳过本回合");
                }

                //按优先级依次行动
                while(!heap.isEmpty() && player.isAlive() && enemy.isAlive()){
                    Character c = heap.extractMax();
                    if(c == player){
                        boolean flee = playerTurn(player, enemy);
                        if(flee){
                            escaped = true;
                            break;
                        }
                        if(!enemy.isAlive()){
                            System.out.println("\uD83C\uDF89 你击败了 " + enemy.name + "！");
                            wins++;
                            break;
                        }
                    }else if(c == enemy){
                        enemyTurn(player, enemy);
                        if(!player.isAlive()){
                            System.out.println("\uD83D\uDE2D 你被 " + enemy.name + " 击败了！");
                            break;
                        }
                    }
                }

                if(escaped) break;

                //回合结束处理状态（减少持续回合、移除过期状态）
                StatusManager.processTurnEnd(player);
                StatusManager.processTurnEnd(enemy);

                if(!player.isAlive() || !enemy.isAlive()){
                    break;
                }

                //没有死亡则继续下一轮
                round++;
            }

            if(escaped) break;

            //跟一个敌人战斗结束
            if(player.isAlive()){
                //恢复血量(20 - 40)的随机值
                int healHP = r.nextInt(21) + 20;
                //恢复魔法值(10 / 20)的随机值
                int healMP = (r.nextInt(2) + 1) * 10;
                player.heal(healHP);
                player.recoverMP(healMP);
                System.out.println("\uD83D\uDC9A 战斗结束！你恢复了 " + healHP + " 点生命值和 " + healMP + " 点魔法值");
                System.out.println("\uD83C\uDFC6 当前胜场: " + wins);
                System.out.println("═══════════════════════════════════════");
            }

            //每胜利3场，提升玩家属性
            if(player.isAlive() && wins > 0 && wins % 3 == 0){
                System.out.println("恭喜！你的属性得到了提升！");
                player.maxHP += 35;
                player.maxMP += 20;
                player.attack += 9;
                player.defense += 6;
                player.speed += 3;
                System.out.println("最大生命值 + 35，最大魔法值 + 20，攻击力 + 9，防御力 + 6，速度 + 3");
                System.out.println("当前属性为：" + player.show());
            }

            //询问玩家是否继续游戏
            if(player.isAlive()){
                System.out.println("是否继续游戏？(y/n)");
                Scanner sc = new Scanner(System.in);
                String choose = sc.next();
                if (choose.equalsIgnoreCase("y")) {
                    count++;
                    continue;
                }else if(choose.equalsIgnoreCase("n")){
                    break;
                }else{
                    System.out.println("没有这个选项，默认继续游戏");
                    continue;
                }
            }
        }

        //游戏结算
        System.out.println("═══════════════════════════════════════");
        System.out.println("游戏结束！");
        System.out.println("总胜场：" + wins);
        System.out.println("感谢游玩文字版格斗游戏");

        //停止虚拟机运行
        System.exit(0);
    }

    //定义一个方法打印血条
    public String getHealthBar(String name, int HP, int maxHP){
        int barLength = 20;

        //计算血条填充长度
        int filled = (int) (HP * 1.0 / maxHP * barLength);

        StringBuilder sb = new StringBuilder();
        sb.append(name).append(": [");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                sb.append("█");
            } else {
                sb.append(" ");
            }
        }

        sb.append("] ").append(HP).append("/").append(maxHP).append(" HP");
        return sb.toString();
    }

    //创建玩家角色
    public HeroCharacter createPlayerCharacter(String username) {
        System.out.println("创建您的角色");
        System.out.println("请输入角色名称：" + username);

        //分配属性点
        int points = 30;
        System.out.println("请分配属性点（共30点）：");
        System.out.println("基础生命值:100（每点 + 10HP)");
        System.out.println("基础魔法值:50（每点 + 10MP）");
        System.out.println("基础攻击力:10（每点 + 2ATK）");
        System.out.println("基础防御力:0（每点 + 1DEF）");
        System.out.println("基础速度:8（每点 + 1SPD）");

        Scanner sc = new Scanner(System.in);

        //定义数组把提示词存起来
        String[] attributes = {"生命值", "魔法值", "攻击力", "防御力", "速度"};

        //定义数组记录三个属性分配的属性点
        int[] values = new int[5];

        for(int i = 0; i < attributes.length; i++){
            System.out.println("分配点数到" + attributes[i] + "(剩余点数：" + points + ")：");
            int input = sc.nextInt();

            if(input < 0){
                System.out.println("无效输入！默认分配0点");
                input = 0;
            }
            if(input > points) {
                System.out.println("属性点不足！剩余属性点全部分配到：" + attributes[i]);
                input = points;
            }

            //计算剩余属性点
            points -= input;
            //记录属性点
            values[i] = input;
        }

        //创建角色
        HeroCharacter player = new HeroCharacter(
                username,  //角色名称
                100 + values[0] * 10,  //生命值
                50 + values[1] * 10,  //魔法值
                10 + values[2] * 2,  //攻击力
                0 + values[3] * 1,  //防御力
                8 + values[4] * 1  //速度
        );

        //添加技能
        player.skillList.add("普通攻击");
        player.skillList.add("强力一击");
        player.skillList.add("生命汲取");
        player.skillList.add("防御");

        return player;
    }

    //玩家回合
    public boolean playerTurn(HeroCharacter player, EnemyCharacter enemy) {
        System.out.println("===== 你的回合 =====");
        System.out.println("1. 普通攻击");
        System.out.println("2. 强力一击（消耗10MP）");
        System.out.println("3. 生命汲取（消耗10MP）");
        System.out.println("4. 防御");
        System.out.println("5. 逃跑");
        System.out.println("当前魔力：" + player.MP + "，选择行动（1 - 5）：");
        Scanner sc = new Scanner(System.in);
        String choose = sc.next();
        switch (choose) {
            default:
                System.out.println("没有这个操作，默认使用普通攻击");
            case "1": {
                int damage = calculateDamage(player.attack, enemy.defense);
                boolean crit = Math.random() < player.critRate;
                if (crit) {
                    damage = (int) (damage * player.critMultiplier);
                    System.out.println("💥 暴击！");
                }
                System.out.println("⚔\uFE0F  你对 " + enemy.name + " 使用了普通攻击，造成 " + damage + " 点伤害！\n");
                enemy.takeDamage(damage);
                break;
            }
            case "2": {
                if(player.MP >= 10){
                    player.MP -= 10;
                    int damage = calculateDamage((int) (player.attack * 1.8), enemy.defense);

                    boolean crit = Math.random() < player.critRate;
                    if (crit) {
                        damage = (int) (damage * player.critMultiplier);
                        System.out.println("💥 暴击！");
                    }
                    System.out.println("\uD83D\uDCA5 消耗10MP，你对 " + enemy.name + " 使用了强力一击，造成 " + damage + " 点伤害！");
                    enemy.takeDamage(damage);
                }else{
                    System.out.println("魔力不足，攻击失败");
                }
                break;
            }
            case "3": {
                if(player.MP >= 10){
                    player.MP -= 10;
                    Random r = new Random();
                    int healHP = r.nextInt(21) + 5;
                    player.heal(healHP);
                    System.out.println("\uD83D\uDC9A 消耗10MP，你使用了生命汲取，恢复了 " + healHP + " 点生命值，吸取了" + enemy.name + "的" + healHP + "点生命值");
                    enemy.takeDamage(healHP);
                }else{
                    System.out.println("魔力不足，吸取生命失败");
                }
                break;
            }
            case "4": {
                StatusManager.addStatus(player, "护盾", 15, 1);
                System.out.println("🛡 你进入了防御姿态，获得 15 点护盾！");
                break;
            }
            case "5": {
                double rate = 0.5 + (player.speed - enemy.speed) * 0.03;
                if (rate > 0.9) rate = 0.9;
                if (rate < 0.1) rate = 0.1;
                if (Math.random() < rate) {
                    System.out.println("🏃 逃跑成功！");
                    return true;
                } else {
                    System.out.println("❌ 逃跑失败！");
                }
                break;
            }
        }
        return false;
    }

    //敌人回合
    public void enemyTurn(HeroCharacter player, EnemyCharacter enemy) {
        System.out.println("===== " + enemy.name + " 的回合 =====");
        //计算是普通攻击还是技能攻击 各50%的概率
        String action = "普通攻击";

        Random r = new Random();
        int num = r.nextInt(2);
        if(num == 1) action = enemy.skill;

        switch (action){
            case "普通攻击":{
                System.out.println("敌人采取了普通攻击");
                int damage = calculateDamage(enemy.attack, player.defense);
                System.out.println("⚔\uFE0F " + enemy.name + " 对你使用了普通攻击，造成 " + damage + " 点伤害！");
                player.takeDamage(damage);
                break;
            }
            case "猛击": {
                System.out.println("当前战士采取了猛击");
                int damage = calculateDamage((int) (enemy.attack * 1.5), player.defense);
                System.out.println("\uD83D\uDCA5 " + enemy.name + " 对你使用了猛击，造成 " + damage + " 点伤害！");
                player.takeDamage(damage);
                //30%概率附加眩晕
                if (Math.random() < 0.3) {
                    StatusManager.addStatus(player, "眩晕", 0, 1);
                }
                break;
            }
            case "快速攻击": {
                System.out.println("当前刺客采取了快速攻击");
                int damage = 0;
                for (int i = 0; i < 3; i++) {
                    int temp = calculateDamage(enemy.attack / 2, player.defense);
                    damage += temp;
                }
                System.out.println("⚔\uFE0F " + enemy.name + " 对你使用了快速攻击，造成 " + damage + " 点伤害！");
                player.takeDamage(damage);
                break;
            }
            case "防御姿态": {
                System.out.println("当前坦克采取了防御姿态");
                enemy.defending = true;
                System.out.println(enemy.name + " 摆出了防御姿态！");
                break;
            }
            case "火球术": {
                System.out.println("当前法师采取了火球术");
                int damage = calculateDamage((int) (enemy.attack * 1.8), player.defense);
                System.out.println("\uD83D\uDCA5 " + enemy.name + " 对你使用了火球术，造成 " + damage + " 点伤害！");
                player.takeDamage(damage);
                //30%概率附加中毒
                if (Math.random() < 0.3) {
                    StatusManager.addStatus(player, "中毒", 5, 3);
                }
                break;
            }
        }
    }

    //计算伤害
    public int calculateDamage(int attack, int defense) {
        int damage = attack - defense;
        if (damage < 1) {
            damage = 1;
        }
        return damage;
    }
}
