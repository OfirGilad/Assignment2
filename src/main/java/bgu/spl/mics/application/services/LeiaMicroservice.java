package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import java.util.ArrayList;
import java.util.List;

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
	private final Future[] futureAttacks;
	private final Diary diary;
	private Boolean bombDestroyerEventResult;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.attackEvents = new AttackEvent[attacks.length];
		this.futureAttacks = new Future[attacks.length];
        diary = Diary.getInstance();
        bombDestroyerEventResult = false;
        for (int i = 0; i < attacks.length; i ++) {
            attackEvents[i] = new AttackEvent(super.getName(), attacks[i]);
        }
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (broadcastCallBack.getMissionProgress()) {
                diary.setLeiaTerminate(System.currentTimeMillis());
                terminate();
            }
        });
        //Attack Phase
        for (int i = 0; i < attacks.length; i++) {
            futureAttacks[i] = sendEvent(attackEvents[i]);
        }
        int attacksIndex = 0;
        while (attacksIndex < attacks.length) {
            Object attackResult = futureAttacks[attacksIndex].get();
            if (attackResult.equals(false)){
                attackEvents[attacksIndex] = new AttackEvent(super.getName(), attacks[attacksIndex]);
            }
            else {
                attacksIndex++;
            }
        }
        //BombDestroyer Phase (Lando sends Deactivation request to R2D2)
        while (!bombDestroyerEventResult) {
            BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent(super.getName());
            Future<Boolean> eventResult = sendEvent(bombDestroyerEvent);
            bombDestroyerEventResult = eventResult.get();
        }
    }
}