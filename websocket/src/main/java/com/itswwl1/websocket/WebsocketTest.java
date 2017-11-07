package com.itswwl1.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Administrator
 *
 * maven项目添加websocket
 * http://www.cnblogs.com/likun10579/p/5450209.html
 * 
 * 
 * 发送到单个客户端
 * 
 */
@ServerEndpoint("/hello")
public class WebsocketTest {
    public WebsocketTest(){
        System.out.println("WebsocketTest..");
    }

    @OnOpen
    public void onopen(Session session){
        System.out.println("连接成功");
        try {
            session.getBasicRemote().sendText("hello client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @OnClose
    public void onclose(Session session){
        System.out.println("close....");
        
    }
     @OnMessage      
    public void onsend(Session session,String msg){
        try {
        	System.out.println(session.getId());
            session.getBasicRemote().sendText("client"+session.getId()+"say:"+msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
     
}
