package com.example;

import com.example.handlers.EchoServerHandler;
import com.example.handlers.EchoServerHandler2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;
    public EchoServer(int port){
        this.port = port;
    }
    public static void main(String[] args) throws InterruptedException {
//        if(args.length!=1){
//            System.err.println("Usage:"+EchoServer.class.getSimpleName()+" <port>");
//            return;
//        }
        int port = 8880;//Integer.parseInt(args[0]);
        EchoServer server = new EchoServer(port);
        server.start();

    }

    private void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)//可在该类中查看bind,read等的方法的具体实现
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler2());
//                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind();//会调用java.nio的selector.register和ServerSocketChannel.bind
            f.sync();
            Channel fc = f.channel();
            ChannelFuture fcf = fc.closeFuture();
            fcf.sync();//会调用object.wait()方法，等待事件发生时调用notifyAll,用到同一个DefaultPromise实例
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}
