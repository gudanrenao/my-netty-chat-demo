package com.zwenit.chat.demo.use_other_eventexecutor_write;

import com.zwenit.chat.utils.PrintUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author wen.zhang
 * @date 2020/8/24 13:52
 **/
public class Server {

    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup(1);

        ServerBootstrap b = new ServerBootstrap();

        b.group(parentGroup, childGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast(new LineBasedFrameDecoder(2048));
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new MyHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = b.bind(new InetSocketAddress(6789)).sync();
            System.out.println("服务端启动成功");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }

    private static class MyHandler extends SimpleChannelInboundHandler<String> {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            PrintUtils.printWithTime("客户端下线啦。。。");
            ctx.channel().close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            PrintUtils.printWithTime("收到客户端消息 ： " + msg);
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("服务回复消息\n".getBytes()));
            }).start();
        }
    }
}
