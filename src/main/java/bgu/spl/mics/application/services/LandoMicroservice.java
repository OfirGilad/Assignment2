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
                System.out.println(getName() + " has left the channel gracefully");
                terminate();
            }
        });
        subscribeEvent(BombDestroyerEvent.class, eventCallBack -> {
            System.out.println(getName() + " received a BombDestroyerEvent");
            deactivationEventResult = eventCallBack.getDeactivationResult().get();

            //In case DeactivationEvent failed Lando resend it
            while (!deactivationEventResult) {
                DeactivationEvent deactivationEvent = new DeactivationEvent(super.getName());
                Future<Boolean> eventResult = sendEvent(deactivationEvent);
                deactivationEventResult = eventResult.get();
            }
            System.out.println(getName() + " received the update from R2D2 via future channel that the star destroyer ship shields are down");
            try {
                System.out.println(getName() + " started a BombDestroyerEvent");
                Thread.sleep(duration);
                System.out.println(getName() + " completed the BombDestroyerEvent");
                complete(eventCallBack, true);
                System.out.println(getName() + " sent broadcast to all that the mission is completed");
                MissionProgressBroadcast missionCompletedBroadcast = new MissionProgressBroadcast(true);
                sendBroadcast(missionCompletedBroadcast);
            } catch (InterruptedException e) {
                System.out.println(getName() + " failed to complete the BombDestroyerEvent, aborting mission...");
                complete(eventCallBack, false);
            }
        });
        System.out.println(getName() + " has joined the channel");
        Main.waitForAllToSubEvents.countDown();
    }
}