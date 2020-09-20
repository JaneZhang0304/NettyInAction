package com.example.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        System.out.println("CurrentThread: "+ Thread.currentThread().getName());
        System.out.println("Server received:"+in.toString(CharsetUtil.UTF_8));
        System.out.println("Server "+msg+" refCnt:"+in.refCnt());
        ctx.write(in.copy());//怀疑报错时因为同一个ByteBuf被写了两遍

        System.out.println("Server "+msg+" refCnt:"+in.refCnt());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("CurrentThread: "+ Thread.currentThread().getName());
        System.out.println("channelReadComplete====>");
//        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
//                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
