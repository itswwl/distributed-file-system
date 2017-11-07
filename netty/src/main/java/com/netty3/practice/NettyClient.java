package com.netty3.practice;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class NettyClient {
	public static void main(String[] args) {
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new ClientHandler());
			}
		});

		ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 8000));

		future.getChannel().getCloseFuture().awaitUninterruptibly();

		bootstrap.releaseExternalResources();
	}

	private static class ClientHandler extends SimpleChannelHandler {
		private BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			if (e.getMessage() instanceof String) {
				String message = (String) e.getMessage();
				System.out.println("Server发来:"+message);
				System.out.println("\n请输入发给服务器的信息:");
				e.getChannel().write(sin.readLine());

//				System.out.println("\n客户端\t等待客户端输入。。。");
			}

			super.messageReceived(ctx, e);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			System.out.println("\n客户端\t已经与Server建立连接。。。。");
			System.out.println("\n请输入要发送的信息：");
			super.channelConnected(ctx, e);

			e.getChannel().write(sin.readLine());
		}
	}
}
