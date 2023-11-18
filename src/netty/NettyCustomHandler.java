package netty;

import common.Query;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static common.Main.logger;

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

        try {
            Connection connection = Query.createConnection();

            ConcurrentMap<String, Object> map = new ConcurrentHashMap<>();
            for (String key : jsonObject.keySet()) {
                map.put(key, jsonObject.get(key));
            }
            String number = (String) map.get("number");
            String text = (String) map.get("text");

            if (requestURI.equals("/insert")) {

                Query.insertData(connection, number, text);
                sendSuccessResponse(ctx);
            } else if (requestURI.equals("/select")) {

                String result = new Query().selectData(connection, number);
                System.out.println("Selected data: " + result);
                sendSuccessResponse(ctx);
            } else {
                sendNotFoundResponse(ctx);
            }
            //종료
            connection.close();
        } catch (Exception e) {
            logger.error("Error during processing", e);
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
