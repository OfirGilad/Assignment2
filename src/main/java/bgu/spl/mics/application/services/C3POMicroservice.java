package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


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

    public C3POMicroservice() {
        super("C3PO");
        ewoks = Ewoks.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (!broadcastCallBack.getMissionProgress()) {
                Diary.setC3POTerminate(System.currentTimeMillis());
                terminate();
            }
            else {
                subscribeEvent(AttackEvent.class, eventCallBack -> {
                    Attack attack = eventCallBack.getAttack();
                    int attackNumber = 0;
                    while (attackNumber != attack.getSerials().size()) {
                        ewoks.acquireEwok(attack.getSerials().get(attackNumber));
                        try {
                            C3POMicroservice.this.wait((long) (attack.getDuration()));
                            complete(eventCallBack, true);
                            ewoks.releaseEwok(attack.getSerials().get(attackNumber));
                            attackNumber++;
                        }
                        catch (InterruptedException e) {
                            ewoks.releaseEwok(attack.getSerials().get(attackNumber));
                        }
                    }
                    Diary.setC3POFinish(System.currentTimeMillis());
                });
            }
        });
    }
}
