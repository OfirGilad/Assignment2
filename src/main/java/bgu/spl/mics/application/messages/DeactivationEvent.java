package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;


public class DeactivationEvent implements Event<Boolean> {
    private final String senderName;
    private Boolean result;

    public DeactivationEvent(String senderName) {
        this.senderName = senderName;
        this.result = false;
    }

    public void complete () {
        result = true;
    }

    public String getSenderName() {
        return senderName;
    }

    public Boolean getResult() {
        return result;
    }
}