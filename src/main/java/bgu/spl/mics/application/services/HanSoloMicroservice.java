package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.example.messages.ExampleEvent;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEvent.class, eventCallBack -> {
            Attack attack = eventCallBack.getAttack();
            int numberOfCompletedAttacks = 0;
            while (numberOfCompletedAttacks != attack.getSerials().size()) {
                for (int i = 0; i < attack.getSerials().size(); i++) {
                    //Get Ewok

                    try {
                        HanSoloMicroservice.this.wait((long) (attack.getDuration()));
                        complete(eventCallBack, true);
                    } catch (InterruptedException e) {
                        //MissionFailed
                    }
                }
            }
        });
    }
}
