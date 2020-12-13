package bgu.spl.mics.application.services;
import bgu.spl.mics.*;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.HashMap;
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
	private final HashMap<Integer,Future<Boolean>> futureAttacksMap;
	private final Diary diary;
	private Boolean bombDestroyerEventResult;
    private boolean isReady;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.attackEvents = new AttackEvent[attacks.length];
        diary = Diary.getInstance();
        bombDestroyerEventResult = false;
        futureAttacksMap = new HashMap<>();
        isReady = false;
        for (int i = 0; i < attacks.length; i ++) {
            attackEvents[i] = new AttackEvent(super.getName(), attacks[i]);
        }
    }

    @Override
    protected void initialize()  {
        subscribeBroadcast(MissionProgressBroadcast.class, broadcastCallBack -> {
            if (broadcastCallBack.getMissionProgress()) {
                diary.setLeiaTerminate(System.currentTimeMillis());
                System.out.println(getName() + " has left the channel gracefully");
                terminate();
            }
        });

        //Waiting for all the other microservices to complete subscriptions
        while (!isReady) {
            try {
                Main.waitForAllToSubEvents.await();
                isReady = true;
            }
            catch (InterruptedException e) {
                System.out.println(getName() + " failed while waiting for other microservices to complete subscriptions, retrying to wait again...");
            }
        }
        System.out.println(getName() + " has joined the channel");
        System.out.println(getName() + " started sending AttackEvents to HanSolo and C3PO");

        //Attack Phase
        for (int i = 0; i < attacks.length; i++) {
            futureAttacksMap.put(i, sendEvent(attackEvents[i]));
        }
        while (!futureAttacksMap.isEmpty()) {
            for (int i = 0; i < attackEvents.length && !futureAttacksMap.isEmpty(); i++) {
                if (futureAttacksMap.get(i) != null && futureAttacksMap.get(i).isDone()) {
                    Object attackResult = futureAttacksMap.get(i).get();
                    if (attackResult.equals(false)) {
                        AttackEvent attackEvent = new AttackEvent(super.getName(), attacks[i]);
                        futureAttacksMap.replace(i, futureAttacksMap.get(i), sendEvent(attackEvent));
                    }
                    else {
                        futureAttacksMap.remove(i);
                    }
                }
            }
        }
        System.out.println("The attacks on the star destroyer ship have finished");
        System.out.println("A DeactivationEvent was sent to R2D2");

        //Deactivation Phase (Leia send the DeactivationEvent to R2D2 and the related future to Lando to check)
        DeactivationEvent deactivationEvent = new DeactivationEvent(super.getName());
        Future<Boolean> R2D2Result = sendEvent(deactivationEvent);

        //BombDestroyer Phase (Lando gets R2D2 result from the future sent by the BombDestroyerEvent)
        while (!bombDestroyerEventResult) {
            BombDestroyerEvent bombDestroyerEvent = new BombDestroyerEvent(super.getName(), R2D2Result);
            Future<Boolean> eventResult = sendEvent(bombDestroyerEvent);
            bombDestroyerEventResult = eventResult.get();
        }
    }
}