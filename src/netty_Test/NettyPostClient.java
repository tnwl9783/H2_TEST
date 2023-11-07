package netty_Test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;


//별거아님.
public class NettyPostClient {
    private final String host;
    private final int port;

    public NettyPostClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpResponse>() {
                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
                                    System.out.println("Response from server: " + msg.content().toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            URI uri = new URI("http", null, host, port, null, null, null);
            String json = "{\"idx\":\"value1\",\"appId\":\"value2\"," +
                    "\"agentId\":\"value2\",\"rcvYn\":\"value2\"," +
                    "\"osType\":\"value2\",\"regId\":\"value1\",\"fcmToken\":\"value2\"}"; // Your JSON payload
            FullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1,
                    HttpMethod.POST,
                    uri.toASCIIString(),
                    Unpooled.copiedBuffer(json, CharsetUtil.UTF_8)
            );
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());


            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(request);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        String host = "localhost"; // Change this to the server's host
        int port = 8080; // Change this to the server's port
        new NettyPostClient(host, port).run();
    }
}