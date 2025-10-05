package web.websockets;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that allows backend services (like StockService)
 * to send messages to all connected WebSocket clients.
 */
public class InventorySocketNotifier {

    // Thread-safe global set of sessions
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    // Called from @OnOpen in InventorySocket
    public static void addSession(Session session) {
        sessions.add(session);
    }

    // Called from @OnClose in InventorySocket
    public static void removeSession(Session session) {
        sessions.remove(session);
    }

    // Called from StockService (backend)
    public static void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        System.err.println("⚠️ WebSocket send failed: " + e.getMessage());
                    }
                }
            }
        }
    }
}
