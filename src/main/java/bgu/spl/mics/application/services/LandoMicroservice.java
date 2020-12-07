package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private final long duration;
    private final Diary diary;
    private Boolean deactivationEventResult;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        diary = Diary.getInstance();
        deactivationEventResult = false;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (broadcastCallBack.getMissionProgress()) {
                diary.setLandoTerminate(System.currentTimeMillis());
                terminate();
            }
        });
        subscribeEvent(BombDestroyerEvent.class, eventCallBack -> {
            while (!deactivationEventResult) {
                DeactivationEvent deactivationEvent = new DeactivationEvent(super.getName());
                Future<Boolean> eventResult = sendEvent(deactivationEvent);
                deactivationEventResult = eventResult.get();
            }
            try {
                Thread.sleep(duration);
                complete(eventCallBack, true);
                MissionProgressBroadcast missionCompletedBroadcast = new MissionProgressBroadcast(true);
                sendBroadcast(missionCompletedBroadcast);
            } catch (InterruptedException e) {
                System.out.println(getName() + " failed to complete the BombDestroyerEvent, aborting mission...");
                complete(eventCallBack, false);
            }

        });
        Main.waitForAllToSubEvents.countDown();
    }
}