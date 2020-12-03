package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.application.passiveObjects.Diary;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private final Attack[] attacks;
	private final AttackEvent[] attackEvents;
	private final Future<Boolean>[] futureAttacks;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.attackEvents = new AttackEvent[attacks.length];
		this.futureAttacks = new Future[attacks.length];

        for (int i = 0; i < attacks.length; i ++) {
            attackEvents[i] = new AttackEvent(super.getName(), attacks[i]);
        }
    }

    @Override
    protected void initialize() {
        //Declaring the Start
        sendBroadcast(new MissionProgressBroadcast(true));
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {

            if(!broadcastCallBack.getMissionProgress()) {
                Diary.setLeiaTerminate(System.currentTimeMillis());
                terminate();
            }
            else {

                //Attack Phase
                for (int i = 0; i < attacks.length; i++) {
                    futureAttacks[i] = sendEvent(attackEvents[i]);
                }
                int numberOfFinishedAttacks = 0;
                while (numberOfFinishedAttacks != attackEvents.length) {
                    for (int i = 0; i < attacks.length; i++) {
                        if (futureAttacks[i] != null) {
                            if (!futureAttacks[i].isDone()) {
                                Boolean resolved = futureAttacks[i].get((long) (attacks[i].getDuration()), TimeUnit.MILLISECONDS);
                                if (resolved != null) {
                                    futureAttacks[i].resolve(resolved);
                                    numberOfFinishedAttacks++;
                                }
                            }
                        }
                    }
                }

                //Deactivation Phase
                DeactivationEvent deactivationEvent = new DeactivationEvent(super.getName());
                Future<Boolean> futureDeactivation = sendEvent(deactivationEvent);
                boolean isDeactivationEventInProgress = true;
                while (isDeactivationEventInProgress) {
                    if (futureDeactivation != null) {
                        Boolean resolved = futureDeactivation.get();
                        futureDeactivation.resolve(resolved);
                        isDeactivationEventInProgress = false;
                    }
                }

                //BombDestroyer Phase
                BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent(super.getName());
                Future<Boolean> futureBombDestroyer = sendEvent(bombDestroyerEvent);
                boolean isBombDestroyerEventInProgress = true;
                while (isBombDestroyerEventInProgress) {
                    if (futureBombDestroyer != null) {
                        Boolean resolved = futureBombDestroyer.get();
                        futureBombDestroyer.resolve(resolved);
                        isBombDestroyerEventInProgress = false;
                    }
                }

                //Mission Complete
                sendBroadcast(new MissionProgressBroadcast(false));
            }
        });
    }
}