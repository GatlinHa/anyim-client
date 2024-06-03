package com.hibob.anyim.client.handler;

import com.hibob.anyim.client.consts.Const;
import com.hibob.anyim.client.protobuf.Header;
import com.hibob.anyim.client.protobuf.Msg;
import com.hibob.anyim.client.protobuf.MsgType;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ClientHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        if (msg.getHeader().getMsgType() == MsgType.HELLO)
        {
            log.info("<<===收到了Netty服务端答复的Hello消息");
        }
        else if (msg.getHeader().getMsgType() == MsgType.CHAT) {
            // 这里是只做收到的消息展示
            String fromId = msg.getBody().getFromId();
            String fromClient = msg.getBody().getFromClient();
            String content = msg.getBody().getContent();
            log.info("<<===收到【{}】发过来的的消息：{}", fromId + "@" + fromClient, content);
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE.equals(evt)) {
            log.info("handshake success!");
            Header header = Header.newBuilder()
                    .setMagic(Const.MAGIC)
                    .setVersion(0)
                    .setMsgType(MsgType.HELLO)
                    .setIsExtension(false)
                    .build();
            Msg msg = Msg.newBuilder().setHeader(header).build();
            ctx.writeAndFlush(msg).addListener(future -> {
               if (future.isSuccess()) {
                   log.info("send hello success!");
               }
               else {
                   log.info("send hello fail! cause is {}", future.cause());
               }
            });

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("ClientHandler exceptionCaught {}", cause.toString());
    }
}
