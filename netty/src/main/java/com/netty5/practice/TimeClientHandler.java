package com.netty5.practice;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {

    private ByteBuf buf;

    /**
     * 开始处理的时候触发
     * @param ctx
     */

    @Override

    public void handlerAdded(ChannelHandlerContext ctx) {

        buf = ctx.alloc().buffer(4); // 分配4个字节的空间给ByteBuf

    }

 

    /**

     * 处理结束的时候触发

     *

     * @param ctx

     */

    @Override

    public void handlerRemoved(ChannelHandlerContext ctx) {

        buf.release();//释放ByteBuf的空间
        buf = null;

    }

 

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	System.out.println("========"+msg);
        ByteBuf m = (ByteBuf) msg;

        /**
         * 所有接收的数据都应该被累积在buf变量里
         */

        buf.writeBytes(m);

        m.release();

        /**
         * 处理器必须检查buf变量是否有足够的数据，在这个例子中是4个字节，
         * 然后处理实际的业务逻辑。
         * 否则，Netty会重复调用channelRead()当有更多数据到达直到4个字节的数据被积累。
         */

        if (buf.readableBytes() >= 4) {
            long currentTimeMillis = (buf.readInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }

    }

 

    @Override

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();
        ctx.close();

    }

}
