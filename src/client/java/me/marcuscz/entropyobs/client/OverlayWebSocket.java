package me.marcuscz.entropyobs.client;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class OverlayWebSocket extends WebSocketServer {

    public OverlayWebSocket() {
        super(new InetSocketAddress(8081));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        EntropyOBSClient.LOGGER.info("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {
        EntropyOBSClient.LOGGER.info("WebSocket server started on port: " + getPort());
    }
}
