package bgu.spl.mics.application.services;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private final Ewoks ewoks;
    private final Diary diary;

    public HanSoloMicroservice() {
        super("Han");
        ewoks = Ewoks.getInstance();
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (broadcastCallBack.getMissionProgress()) {
                diary.setHanSoloTerminate(System.currentTimeMillis());
                System.out.println(getName() + " has left the channel gracefully");
                terminate();
            }
        });
        subscribeEvent(AttackEvent.class, eventCallBack -> {
            System.out.println(getName() + " received an AttackEvent");
            Attack HanSoloAttack = eventCallBack.getAttack();
            HanSoloAttack.getSerials().sort(Integer::compareTo);
            if (ewoks.acquireEwoks(HanSoloAttack.getSerials())) {
                try {
                    System.out.println(getName() + " started an AttackEvent");;
                    Thread.sleep(HanSoloAttack.getDuration());
                    diary.setHanSoloFinish(System.currentTimeMillis());
                    System.out.println(getName() + " completed the AttackEvent");
                    diary.setNumberOfAttacks(diary.getNumberOfAttacks().get() + 1);
                    complete(eventCallBack, true);
                    ewoks.releaseEwoks(HanSoloAttack.getSerials());
                } catch (InterruptedException e) {
                    System.out.println(getName() + " failed to complete the AttackEvent, aborting mission...");
                    ewoks.releaseEwoks(HanSoloAttack.getSerials());
                    complete(eventCallBack, false);
                }
            }
            else {
                System.out.println(getName() + " failed while waiting to acquire ewoks, aborting mission...");
                complete(eventCallBack, false);
            }
        });
        System.out.println(getName() + " has joined the channel");
        Main.waitForAllToSubEvents.countDown();
    }
}