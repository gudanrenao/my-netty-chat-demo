package com.zwenit.chat.demo.use_other_eventexecutor_write;

import com.zwenit.chat.utils.PrintUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author wen.zhang
 * @date 2020/8/24 13:34
 **/
public class Client {

    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();

        EventLoopGroup loopGroup = new NioEventLoopGroup();

        b.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LineBasedFrameDecoder(2048));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new MyHandler());
                    }
                })
        ;

        try {
            ChannelFuture channelFuture = b.connect(new InetSocketAddress(6789)).sync();
            System.out.println("客户端启动成功");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            loopGroup.shutdownGracefully();
        }
    }

    private static class MyHandler extends SimpleChannelInboundHandler<String> {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello,我是客户端哦\n".getBytes()));
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            PrintUtils.printWithTime("收到服务端的响应： " + msg);
        }
    }
}
