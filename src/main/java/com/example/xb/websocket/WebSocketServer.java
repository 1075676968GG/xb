//package com.example.xb.websocket;
//
//import org.springframework.stereotype.Component;
//
//import javax.websocket.OnClose;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author liao
// * @date 2020/12/5 11:02
// * @Description
// */
//@Component
//@ServerEndpoint("/websocket/{username}")
//public class WebSocketServer {
//    // 当前在线人数
//    private static Integer onlineCount = 0;
//
//    // 当前登录人的用户名
//    private static String username = "";
//
//    // 存储所有的回话对象
//    private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
//
//    // 日期格式化
//    private static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    @OnOpen
//    public void open(Session session, @PathParam("username") String username) {
//
//        // 添加到回话对象中
//        sessions.put(username, session);
//
//        this.username = username;
//
//        // 在线人数+1
//        addOnlineCount();
//
//        String msg=username + "进入了会议室;当前聊天室人数: " + getOnlineCount() + ";--------" + format.format(new Date()) + "<br/>";
//
//        // 群发消息
//        sendMsg(msg);
//    }
//
//
//    @OnClose
//    public void close(@PathParam("username") String username) {
//
//        // 添加到回话对象中
//        sessions.remove(username);
//
//        // 在线人数-1
//        subOnlineCount();
//
//        String msg=username + "退出了会议室;当前聊天室人数: " + getOnlineCount() + ";--------" + format.format(new Date()) + "<br/>";
//
//        // 群发消息
//        sendMsg(msg);
//    }
//
//    @OnMessage
//    public void message(String message) {
//
//        System.out.println("收到来自窗口" + username + "的信息:" + message + "<br/>");
//
//        // 广播消息
//        sendMsg(message);
//    }
//
//    /**
//     * 群发消息
//     *
//     * @param msg
//     */
//    public void sendMsg(String msg) {
//
//        for (String username : sessions.keySet()) {
//            Session session = sessions.get(username);
//
//            if (session != null) {
//                session.getAsyncRemote().sendText(msg);
//            }
//        }
//    }
//
//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    public static synchronized void addOnlineCount() {
//        WebSocketServer.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        WebSocketServer.onlineCount--;
//    }
//}
