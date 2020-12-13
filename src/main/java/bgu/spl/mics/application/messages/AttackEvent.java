package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

public class AttackEvent implements Event<Boolean>  {
    private final String senderName;
    private final Attack attack;
    private Boolean result;

    public AttackEvent(String senderName, Attack attack) {
        this.senderName = senderName;
        this.attack = attack;
        this.result = false;
    }

    public void complete () {
        result = true;
    }

    public String getSenderName() {
        return senderName;
    }

    public Attack getAttack() {
        return attack;
    }

    public Boolean getResult() {
        return result;
    }
}