package com.example.xb.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/xb_websocket/{userId}")
public class XBWebSocket {

    public XBWebSocket() {
        System.out.println("XBWebSocket初始化了");
    }

    // 在线人数
    private static Integer onlineCount = 0;

    // 与服务器保持连接的会话对象
    private Session session;

    // 存储当前所有的会话
    public static final Map<Long, Session> sessions = new ConcurrentHashMap();

    /**
     * 与客户端建立连接执行的方法
     *
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {

        this.session = session;

        // 把会话对象存储起来
        sessions.put(userId, session);

        // 在线人数+1
        addOnlineCount();

        System.out.println("当前会议人数: " + getOnlineCount());
    }

    /**
     * 与客户端断开连接执行的方法
     *
     * @param session
     * @param userId
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId) {

        // 把当前用户移除会议
        sessions.remove(userId);

        // 会议人数-1
        subOnlineCount();

        System.out.println("当前会议人数: " + getOnlineCount());
    }


    @OnMessage
    public void message(String message) {

        // 广播消息
        sendMessage(message);
    }

    /**
     * 群发消息
     */
    public static void sendMessage(String message) {

        for (Long userId : sessions.keySet()) {

            Session session = sessions.get(userId);

            session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * 单发消息
     *
     * @param userId
     * @param message
     */
    public static void sendMessage(Long userId, String message) {

        Session session = sessions.get(userId);

        if (session != null) {
            session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * 群发消息(指定某个人不发)
     *
     * @param userId
     * @param message
     */
    public static void sendMessageNotUser(Long userId, String message) {

        for (Long tempId : sessions.keySet()) {

            if (tempId.longValue() != userId.longValue()) {
                Session session = sessions.get(tempId);

                if (session != null) {
                    session.getAsyncRemote().sendText(message);
                }
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        XBWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        XBWebSocket.onlineCount--;
    }
}