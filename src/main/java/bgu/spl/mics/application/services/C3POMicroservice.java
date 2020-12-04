package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.concurrent.CountDownLatch;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private final Ewoks ewoks;
    private final CountDownLatch waitForAllToSubEvents;
    private final Diary diary;

    public C3POMicroservice(CountDownLatch waitForAllToSubEvents) {
        super("C3PO");
        ewoks = Ewoks.getInstance();
        this.waitForAllToSubEvents = waitForAllToSubEvents;
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (!broadcastCallBack.getMissionProgress()) {
                diary.setC3POTerminate(System.currentTimeMillis());
                terminate();
            }
        });
        subscribeEvent(AttackEvent.class, eventCallBack -> {
            Attack C3POAttack = eventCallBack.getAttack();
            int attackNumber = 0;
            boolean isDone = false;
            while (!isDone) {
                if (attackNumber != C3POAttack.getSerials().size()) {
                    ewoks.acquireEwok(C3POAttack.getSerials().get(attackNumber));
                    try {
                        Thread.sleep(C3POAttack.getDuration());
                        complete(eventCallBack, true);
                        diary.setC3POFinish(System.currentTimeMillis());
                        ewoks.releaseEwok(C3POAttack.getSerials().get(attackNumber));
                        attackNumber++;
                    } catch (InterruptedException e) {
                        System.out.println(getName() + " failed to complete the attack event... Releasing ewok");
                        ewoks.releaseEwok(C3POAttack.getSerials().get(attackNumber));
                    }
                }
                else {
                    isDone = true;
                    diary.incrementTotalAttacks();
                }
            }
        });
        waitForAllToSubEvents.countDown();
    }
}
