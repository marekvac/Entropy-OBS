package me.marcuscz.entropyobs.client.mixin;

import me.juancarloscp52.entropy.client.EntropyClient;
import me.juancarloscp52.entropy.client.VotingClient;
import me.marcuscz.entropyobs.client.EntropyOBSClient;
import me.marcuscz.entropyobs.client.JsonVotingEvent;
import me.marcuscz.entropyobs.client.JsonVotingPacket;
import me.marcuscz.entropyobs.client.OverlayWebSocket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VotingClient.class)
public abstract class VotingClientMixin {

    @Shadow
    int totalVotesCount;

    @Shadow
    List<Text> events;

    @Shadow
    int[] totalVotes;

    @Shadow
    int voteID;

    @Shadow
    public abstract int getColor(int alpha);


    /**
     * @author MarcusCZ
     * @reason Sends voting data to the OBS client via WebSocket
     */
    @Overwrite
    public void render(DrawContext context) {
        long now = System.currentTimeMillis();
        if (now - EntropyOBSClient.instance.lastSent < EntropyOBSClient.SEND_DELAY) {
            return;
        }
        EntropyOBSClient.instance.lastSent = now;
        if (EntropyOBSClient.socket != null) {
            int altOffset = this.voteID % 2 == 0 && EntropyClient.getInstance().integrationsSettings.integrationType != 2 ? 4 : 0;
            JsonVotingEvent[] jsonEvents = new JsonVotingEvent[4];
            for (int i = 0; i < 4; i++) {
                double ratio = this.totalVotesCount > 0 ? (double)this.totalVotes[i] / (double)this.totalVotesCount : (double)0.0F;
                String eventText;
                eventText = Text.stringifiedTranslatable(this.events.get(i).getString()).getString();
                jsonEvents[i] = new JsonVotingEvent(altOffset + i + 1, eventText, MathHelper.floor(ratio * (double)100.0F));
            }
            JsonVotingPacket packet = new JsonVotingPacket(this.totalVotesCount, this.getColor(150), jsonEvents);
            EntropyOBSClient.socket.broadcast(packet.toJson());
        }
    }

    @Inject(method = "enable", at = @At("TAIL"))
    public void onEnable(CallbackInfo ci) {
        if (EntropyOBSClient.socket == null) {
            EntropyOBSClient.socket = new OverlayWebSocket();
            EntropyOBSClient.socket.start();
        }
    }

    @Inject(method = "disable", at = @At("TAIL"))
    public void onDisable(CallbackInfo ci) {
        if (EntropyOBSClient.socket != null) {
            try {
                EntropyOBSClient.socket.stop();
            } catch (InterruptedException e) {
                EntropyOBSClient.LOGGER.error("EntropyOBSClient.socket interrupted while stopping:", e);
            }
            EntropyOBSClient.socket = null;
        }
    }


}
