package org.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class ChannelBootstrap {
    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup group1 = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(group, group1)
                .channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    ChannelFuture connectFuture;
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Bootstrap bootstrap = new Bootstrap();
                        bootstrap.channel(NioSocketChannel.class).handler(
                                new SimpleChannelInboundHandler<ByteBuf>() {
                                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                        System.out.println("Child bootstrap received data");
                                    }
                                }
                        );
                        bootstrap.group(ctx.channel().eventLoop());
                        connectFuture = bootstrap.connect(new InetSocketAddress("www.manning.com",80));
                        connectFuture.addListener(new ChannelFutureListener() {
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(connectFuture.isSuccess()){
                                    System.out.println("connectFuture connected");
                                }else{
                                    System.err.println("connectFuture attempt failed");
                                    connectFuture.cause().printStackTrace();
                                }
                            }
                        });
                    }
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        System.out.println("child channel read: "+msg.toString(CharsetUtil.UTF_8));
                        if(connectFuture.isDone()){
                            System.out.println("connectFuture is Done");
                        }
                    }
                });
        ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(8080));
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess())
                    System.out.println("Server bound");
                else{
                    System.err.println("Bound attempt failed");
                    future.cause().printStackTrace();
                }
            }
        });
        future.channel().closeFuture().sync();
        group.shutdownGracefully().syncUninterruptibly();
        group1.shutdownGracefully().syncUninterruptibly();
    }
}
