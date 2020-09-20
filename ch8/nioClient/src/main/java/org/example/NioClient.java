package org.example;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

public class NioClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx){
                        System.out.println("active===>");
                    }
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println("Received data!");
                        System.out.println("received: "+msg.toString(CharsetUtil.UTF_8));
                    }
                });

        System.out.println("invoke connect===>");
        final ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("www.manning.com",80));
        System.out.println("addListener===>");
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println(future==channelFuture);//true
                if(future.isSuccess()){
                    System.out.println("Connection established!");
                }else{
                    System.err.println("Connection attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
        //是谁close的channel啊？！
        channelFuture.channel().closeFuture().syncUninterruptibly();

        Future<?> future = group.shutdownGracefully();
        future.syncUninterruptibly();
    }
}
