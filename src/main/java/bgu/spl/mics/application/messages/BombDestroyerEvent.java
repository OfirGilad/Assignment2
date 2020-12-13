package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class BombDestroyerEvent implements Event<Boolean> {
    private final String senderName;
    private Boolean result;
    private final Future<Boolean> DeactivationResult;

    public BombDestroyerEvent(String senderName, Future<Boolean> DeactivationResult) {
        this.senderName = senderName;
        this.result = false;
        this.DeactivationResult = DeactivationResult;
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

    public Future<Boolean> getDeactivationResult() {
        return DeactivationResult;
    }
}
