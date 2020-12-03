package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.MissionProgressBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

import java.util.concurrent.CountDownLatch;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private final long duration;
    private final CountDownLatch waitForAllToSubEvents;
    private final Diary diary;

    public LandoMicroservice(long duration, CountDownLatch waitForAllToSubEvents) {
        super("Lando");
        this.duration = duration;
        this.waitForAllToSubEvents = waitForAllToSubEvents;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (!broadcastCallBack.getMissionProgress()) {
                diary.setLandoTerminate(System.currentTimeMillis());
                terminate();
            }
        });
        subscribeEvent(BombDestroyerEvent.class, eventCallBack -> {
            boolean isDone = false;
            while (!isDone) {
                try {
                    Thread.sleep(duration);
                    complete(eventCallBack, true);
                    isDone = true;
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitForAllToSubEvents.countDown();
    }
}
