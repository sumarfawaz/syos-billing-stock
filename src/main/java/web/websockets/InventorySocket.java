package web.websockets;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@ServerEndpoint("/inventory-updates")
public class InventorySocket {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("🔗 WebSocket connected: " + sessions.size());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("❌ WebSocket closed: " + sessions.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        broadcast(message); // Optional: rebroadcast messages received
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("⚠️ WebSocket error: " + throwable.getMessage());
    }

    /** ✅ Broadcast a message to all connected sessions */
    public static void broadcast(String message) {
        synchronized (sessions) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
