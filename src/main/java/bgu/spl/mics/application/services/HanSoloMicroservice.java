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
                terminate();
            }
        });
        subscribeEvent(AttackEvent.class, eventCallBack -> {
            Attack HanSoloAttack = eventCallBack.getAttack();
            if (ewoks.acquireEwoks(HanSoloAttack.getSerials())) {
                try {
                    Thread.sleep(HanSoloAttack.getDuration());
                    diary.setHanSoloFinish(System.currentTimeMillis());
                    ewoks.releaseEwoks(HanSoloAttack.getSerials());
                    diary.incrementTotalAttacks();
                    complete(eventCallBack, true);
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
        Main.waitForAllToSubEvents.countDown();
    }
}