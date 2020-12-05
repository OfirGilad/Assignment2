package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

//Sends the status of the Mission:
//"true" = Mission completed and everyone can terminate themselves
//"false" = Mission in progress and everyone can start to act
public class MissionProgressBroadcast implements Broadcast {
    private final Boolean missionProgress;

    public MissionProgressBroadcast(Boolean missionProgress) {
        this.missionProgress = missionProgress;
    }

    public Boolean getMissionProgress() {
        return missionProgress;
    }
}