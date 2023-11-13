package netty;

import common.Main;
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
        //요청 내용 읽음.
        ByteBuf content = request.content();
        // json to string
        String json = content.toString(io.netty.util.CharsetUtil.UTF_8);
        content.release();
        try {
            JSONObject jsonObject = new JSONObject(json);

            // 성공시 status code
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            System.out.println("Status code: " + response.status().code());


            //변수를 클래스로 보내는 방법.
            Main.receiveJSONObject(jsonObject);

            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        } catch (JSONException e) {
            e.printStackTrace();
            // 실패시 status code
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}