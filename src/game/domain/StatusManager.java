package game.domain;

public class StatusManager {

    // 添加状态到角色的状态链表（头插法）
    public static void addStatus(Character c, String type, int value, int turns) {
        StatusEffect node = new StatusEffect(type, turns, value);
        node.next = c.statusHead;
        c.statusHead = node;
        System.out.println(c.name + " 获得了状态：" + type + "（持续 " + turns + " 回合）");
    }

    // 判断角色是否拥有某种状态
    public static boolean hasStatus(Character c, String type) {
        StatusEffect cur = c.statusHead;
        while (cur != null) {
            if (cur.type.equals(type)) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    // 获取某种状态的数值
    public static int getStatusValue(Character c, String type) {
        StatusEffect cur = c.statusHead;
        while (cur != null) {
            if (cur.type.equals(type)) {
                return cur.value;
            }
            cur = cur.next;
        }
        return 0;
    }

    // 处理回合开始时的状态效果（中毒扣血）
    public static void processTurnStart(Character c) {
        if (!c.isAlive()) return;

        StatusEffect cur = c.statusHead;
        while (cur != null) {
            if (cur.type.equals("中毒")) {
                c.takeDamage(cur.value);
                System.out.println("☠ " + c.name + " 受到中毒伤害 " + cur.value + " 点！");
            }
            cur = cur.next;
        }
    }

    // 回合结束时减少持续回合并移除过期状态
    public static void processTurnEnd(Character c) {
        StatusEffect prev = null;
        StatusEffect cur = c.statusHead;

        while (cur != null) {
            cur.remainingTurns--;
            if (cur.remainingTurns <= 0) {
                System.out.println("⏰ " + c.name + " 的状态 " + cur.type + " 已消失");
                if (prev == null) {
                    c.statusHead = cur.next;
                } else {
                    prev.next = cur.next;
                }
                cur = cur.next;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
    }

    // 显示角色所有状态
    public static String showStatus(Character c) {
        if (c.statusHead == null) return "无";

        StringBuilder sb = new StringBuilder();
        StatusEffect cur = c.statusHead;
        while (cur != null) {
            sb.append(cur.type).append("(").append(cur.remainingTurns).append("回合)");
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.toString();
    }
}