package game.domain;

public class StatusEffect {
    public String type;          // 状态类型：中毒、眩晕、护盾、加速
    public int remainingTurns;   // 剩余持续回合数
    public int value;            // 状态数值：中毒伤害、护盾值、加速值
    public StatusEffect next;    // 下一个状态节点

    public StatusEffect(String type, int remainingTurns, int value) {
        this.type = type;
        this.remainingTurns = remainingTurns;
        this.value = value;
        this.next = null;
    }
}