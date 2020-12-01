package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class MissionProgressBroadcast implements Broadcast {
    private String senderId;

    public MissionProgressBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}
