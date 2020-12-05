package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;

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
    private final CountDownLatch waitForAllToSubEvents;
    private final Diary diary;

    public HanSoloMicroservice(CountDownLatch waitForAllToSubEvents) {
        super("Han");
        ewoks = Ewoks.getInstance();
        this.waitForAllToSubEvents = waitForAllToSubEvents;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
           if (!broadcastCallBack.getMissionProgress()) {
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
                    ewoks.releaseEwoks(HanSoloAttack.getSerials());
                    complete(eventCallBack, false);
                }
            }
            else {
                complete(eventCallBack, false);
            }
        });
        waitForAllToSubEvents.countDown();
    }
}