package com.example.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class EchoServerHandler2 extends ChannelInboundHandlerAdapter {
    ByteBuf msg1=null;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        msg1=in;
        System.out.println("CurrentThread: "+ Thread.currentThread().getName());
        System.out.println("Server2 received:"+in.toString(CharsetUtil.UTF_8));
        ctx.write(in);//将接收到的消息写给发送者，而不冲刷出站消息
        System.out.println("Server2 "+msg+"refCnt:"+in.refCnt());
//        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("CurrentThread: "+ Thread.currentThread().getName());
        System.out.println("channelReadComplete2====>");
        System.out.println("ServerComplete2 "+msg1+"refCnt:"+msg1.refCnt());
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
//        ctx.fireChannelReadComplete();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
