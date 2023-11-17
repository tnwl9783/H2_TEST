package netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        // postman에서 전송하면 들어오는 부분
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new NettyCustomHandler());
    }
}
