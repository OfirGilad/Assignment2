package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

//Sends the phase of the Mission:
//"Attack = Time for Han + C3PO to act"
//"Deactivate" = Time for R2D2 to act
//"Bomb" = Time for Lando to act
//"Complete" = Everyone terminate themselves
public class MissionProgressBroadcast implements Broadcast {
    private final String phaseInfo;

    public MissionProgressBroadcast(String phaseInfo) {
        this.phaseInfo = phaseInfo;
    }

    public String getPhaseInfo() {
        return phaseInfo;
    }
}