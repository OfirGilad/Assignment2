package bgu.spl.mics.application.services;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
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
    private final long duration;
    private final Diary diary;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (broadcastCallBack.getMissionProgress()) {
                diary.setR2D2Terminate(System.currentTimeMillis());
                System.out.println(getName() + " has left the channel gracefully");
                terminate();
            }
        });
        subscribeEvent(DeactivationEvent.class, eventCallBack -> {
            System.out.println(getName() + " received a DeactivationEvent");
            try {
                System.out.println(getName() + " started a DeactivationEvent");
                Thread.sleep(duration);
                diary.setR2D2Deactivate(System.currentTimeMillis());
                System.out.println(getName() + " completed the DeactivationEvent");
                complete(eventCallBack, true);
            }
            catch (InterruptedException e) {
                System.out.println(getName() + " failed to acquire complete the DeactivationEvent, aborting mission...");
                complete(eventCallBack, false);
            }
        });
        System.out.println(getName() + " has joined the channel");
        Main.waitForAllToSubEvents.countDown();
    }
}