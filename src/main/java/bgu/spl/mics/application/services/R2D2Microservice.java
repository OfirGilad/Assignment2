package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private final long duration;
    private final CountDownLatch waitForAllToSubEvents;
    private final Diary diary;

    public R2D2Microservice(long duration, CountDownLatch waitForAllToSubEvents) {
        super("R2D2");
        this.duration = duration;
        this.waitForAllToSubEvents = waitForAllToSubEvents;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (!broadcastCallBack.getMissionProgress()) {
                diary.setR2D2Terminate(System.currentTimeMillis());
                terminate();
            }
        });
        subscribeEvent(DeactivationEvent.class, eventCallBack -> {
            boolean isDone = false;
            while (!isDone) {
                try {
                    R2D2Microservice.this.wait(duration);
                    complete(eventCallBack, true);
                    diary.setR2D2Deactivate(System.currentTimeMillis());
                    isDone = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitForAllToSubEvents.countDown();
    }
}
