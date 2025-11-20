package me.marcuscz.entropyobs.client;

import com.google.gson.Gson;

public record JsonVotingPacket(int t, int c, JsonVotingEvent[] e) {
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }


}
