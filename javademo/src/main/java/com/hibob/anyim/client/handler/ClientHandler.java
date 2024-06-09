package com.hibob.anyim.client.handler;

import com.hibob.anyim.client.consts.Const;
import com.hibob.anyim.netty.protobuf.Body;
import com.hibob.anyim.netty.protobuf.Header;
import com.hibob.anyim.netty.protobuf.Msg;
import com.hibob.anyim.netty.protobuf.MsgType;
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
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到【{}】发过来的的CHAT消息：{}，msgId是：{}", fromId + "@" + fromClient, content, msgId);
            Header header = Header.newBuilder()
                    .setMagic(Const.MAGIC)
                    .setVersion(0)
                    .setMsgType(MsgType.READ)
                    .setIsExtension(false)
                    .build();
            Body body = Body.newBuilder()
                    .setFromId(msg.getBody().getToId())
                    .setFromClient(msg.getBody().getToClient())
                    .setToId(msg.getBody().getFromId())
                    .setSeq(1)
                    .setAck(1)
                    .setContent(String.valueOf(msg.getBody().getMsgId()))
                    .build();
            Msg msgRead = Msg.newBuilder().setHeader(header).setBody(body).build();
            ctx.channel().writeAndFlush(msgRead);
        }
        else if (msg.getHeader().getMsgType() == MsgType.READ) {
            String fromId = msg.getBody().getFromId();
            String fromClient = msg.getBody().getFromClient();
            String content = msg.getBody().getContent();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到【{}】发过来的的READ消息：{}，msgId是：{}", fromId + "@" + fromClient, content, msgId);
        }
        else if (msg.getHeader().getMsgType() == MsgType.DELIVERED) {
            String fromId = msg.getBody().getFromId();
            String toId = msg.getBody().getToId();
            String tempMsgId = msg.getBody().getTempMsgId();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===【{}】发给【{}】的tempMsgId={}的消息，已从服务器收到DELIVERED消息，服务端分配的msgId是{}", fromId, toId, tempMsgId, msgId);
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
