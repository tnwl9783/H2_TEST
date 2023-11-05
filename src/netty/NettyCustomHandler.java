package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.json.JSONException;
import org.json.JSONObject;


public class NettyCustomHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        ByteBuf content = request.content();
        String json = content.toString(io.netty.util.CharsetUtil.UTF_8);
        content.release();

        System.out.println("Received data: " + json);

        try {
            JSONObject jsonObject = new JSONObject(json);

            int count = 0;
            while (count < 2) {
                for (String key : jsonObject.keySet()) {
                    System.out.println("Key: " + key + ", Value: " + jsonObject.get(key));
                }
                count++;
            }

            // Send a response with a success status code
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            System.out.println("Status code: " + response.status().code());
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        } catch (JSONException e) {
            e.printStackTrace();
            // Send a response with an error status code
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}