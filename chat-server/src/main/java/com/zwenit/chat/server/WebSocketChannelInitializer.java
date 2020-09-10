package com.zwenit.chat.server;


import com.zwenit.chat.server.handler.HeartBeatHandler;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wen.zhang
 */
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LineBasedFrameDecoder(2048));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());

        //检测空闲，触发时会发送一个{@link IdleStateEvent}事件
        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
        //心跳检测处理器
        pipeline.addLast(new HeartBeatHandler());

        //如果某个ChannelHandler有长时间阻塞操作，可以传递一个EventExecutor，这样的话，就不会放在Channel所属的EventLoop线程执行，避免对其他Channel线程的阻塞
        pipeline.addLast(new DefaultEventLoopGroup(), new SimpleChannelInboundHandler<String>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                System.out.println("收到消息：" + msg);
                //长时间阻塞操作
                TimeUnit.SECONDS.sleep(5);
            }
        });
    }
}
