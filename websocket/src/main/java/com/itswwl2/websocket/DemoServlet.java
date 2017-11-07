package com.itswwl2.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;


/**
 * @author Administrator
 * Java实现WebSocket
 * http://blog.csdn.net/z69183787/article/details/42739895
 * 
 * 必须运行在tomact7下要和pom.xml中的依赖是一个版本，否则不能链接
 * 
 * 实现聊天室功能
 */
@SuppressWarnings("deprecation")
public class DemoServlet extends WebSocketServlet {

    private static final long serialVersionUID = -4853540828121130946L;
    private static ArrayList<MyMessageInbound> mmiList = new ArrayList<MyMessageInbound>();

    protected StreamInbound createWebSocketInbound(String str, HttpServletRequest request) {
        return new MyMessageInbound();
    }

	private class MyMessageInbound extends MessageInbound {
		WsOutbound myoutbound;

        @Override
        public void onOpen(WsOutbound outbound) {
            try {
                System.out.println("Open Client.");
                this.myoutbound = outbound;
                mmiList.add(this);
                outbound.writeTextMessage(CharBuffer.wrap("Hello!"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(int status) {
            System.out.println("Close Client.");
            mmiList.remove(this);
        }

        @Override
        public void onTextMessage(CharBuffer cb) throws IOException {
            System.out.println("Accept Message : " + cb);
            for (MyMessageInbound mmib : mmiList) {
                CharBuffer buffer = CharBuffer.wrap(cb);
                mmib.myoutbound.writeTextMessage(buffer);
                mmib.myoutbound.flush();
            }
        }

        @Override
        public void onBinaryMessage(ByteBuffer bb) throws IOException {
        }

    }

}
