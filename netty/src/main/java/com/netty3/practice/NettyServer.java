package com.netty3.practice;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


/**
 * 
 * 通俗地讲，Netty 能做什么？
 * https://www.zhihu.com/question/24322387
 * 
 * Netty线程模型
 * http://mp.weixin.qq.com/s?__biz=MzA4NDc2MDQ1Nw==&mid=2650238318&idx=1&sn=cabe994e073fa5dfc35ce4c7d343a0d7&chksm=87e18f88b096069e5077c49c70c6fc856da2b6d219f006f2bae5b79e8f74ab5b4bc9244b98f4#rd
 * 
 * */

/**
 * @author Administrator
 * 
 *         http://blog.csdn.net/duyuanhai/article/details/46788389
 *         
 *         请注意  netty各个版本之间存在差别，不同的版本，导致相同的代码不能工作
 *         
 *         这个只能在netty3中工作
 *
 */
public class NettyServer {
	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new StringDecoder(), new StringEncoder(), new ServerHandler());
			}
		});

		Channel bind = bootstrap.bind(new InetSocketAddress(8000));
		System.out.println("Server已经启动，监听端口: " + bind.getLocalAddress() + "， 等待客户端注册。。。");
	}

	private static class ServerHandler extends SimpleChannelHandler {
		private BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			if (e.getMessage() instanceof String) {
				String message = (String) e.getMessage();
				System.out.println("Client发来:" + message);
				System.out.println("\n输入返回给客户端的信息:");

				e.getChannel().write(sin.readLine());
//				e.getChannel().write("Server已收到刚发送的:" + message);

//				System.out.println("\n服务器端\t等待客户端输入。。。");
			}

			super.messageReceived(ctx, e);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			super.exceptionCaught(ctx, e);
		}

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			System.out.println("有一个客户端注册上来了。。。");
			System.out.println("Client:" + e.getChannel().getRemoteAddress());
			System.out.println("Server:" + e.getChannel().getLocalAddress());
			System.out.println("\n服务器端\t等待客户端输入。。。");
			super.channelConnected(ctx, e);
		}
	}
}
