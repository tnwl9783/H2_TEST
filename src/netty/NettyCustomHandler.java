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
        String requestURI = request.uri();
//        System.out.println("requestURI: " + requestURI);

        ByteBuf content = request.content();
        String json = content.toString(io.netty.util.CharsetUtil.UTF_8);
        content.release();


        try {
            JSONObject jsonObject = new JSONObject(json);
            handleRequest(ctx, requestURI, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            sendErrorResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, String requestURI, JSONObject jsonObject) {
        if (requestURI.equals("/insert")) {
            Main.receiveJSONObject(jsonObject);
            sendSuccessResponse(ctx);
        } else if (requestURI.equals("/select")) {
            Main.receiveJSONObject(jsonObject);
            sendSuccessResponse(ctx);
        } else {
            sendNotFoundResponse(ctx);
        }
    }

    private void sendSuccessResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        System.out.println("Status code: " + response.status().code());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendNotFoundResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
