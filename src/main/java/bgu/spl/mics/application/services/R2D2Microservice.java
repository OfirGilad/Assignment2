package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (!broadcastCallBack.getMissionProgress()) {
                Diary.setR2D2Terminate(System.currentTimeMillis());
                terminate();
            }
            else {
                subscribeEvent(DeactivationEvent.class, eventCallBack -> {
                    boolean isDone = false;
                    while (!isDone) {
                        try {
                            R2D2Microservice.this.wait(duration);
                            complete(eventCallBack, true);
                            Diary.setR2D2Deactivate(System.currentTimeMillis());
                            isDone = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
