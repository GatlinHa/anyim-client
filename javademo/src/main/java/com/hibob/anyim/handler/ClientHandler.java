package com.hibob.anyim.handler;

import com.hibob.anyim.consts.Const;
import com.hibob.anyim.netty.protobuf.Body;
import com.hibob.anyim.netty.protobuf.Header;
import com.hibob.anyim.netty.protobuf.Msg;
import com.hibob.anyim.netty.protobuf.MsgType;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Data
public class ClientHandler extends SimpleChannelInboundHandler<Msg> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        if (msg.getHeader().getMsgType() == MsgType.HELLO)
        {
            log.info("<<===收到了Netty服务端答复的Hello消息");
            ctx.channel().attr(AttributeKey.valueOf("hello")).set(true);
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
                    .setTempMsgId(UUID.randomUUID().toString())
                    .build();
            Msg msgRead = Msg.newBuilder().setHeader(header).setBody(body).build();
            ctx.channel().writeAndFlush(msgRead);
        }
        else if (msg.getHeader().getMsgType() == MsgType.GROUP_CHAT) {
            // 这里是只做收到的消息展示
            String fromId = msg.getBody().getFromId();
            String fromClient = msg.getBody().getFromClient();
            String content = msg.getBody().getContent();
            long groupId = msg.getBody().getGroupId();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到【{}】发过来的的GROUP_CHAT消息：{}，群id是：{}，msgId是：{}", fromId + "@" + fromClient, content, groupId, msgId);
            // 暂不发已读消息
        }
        else if (msg.getHeader().getMsgType() == MsgType.READ) {
            String fromId = msg.getBody().getFromId();
            String fromClient = msg.getBody().getFromClient();
            String content = msg.getBody().getContent();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到【{}】发过来的的READ消息，已读的消息内容是：{}，msgId是：{}", fromId + "@" + fromClient, content, msgId);
        }
        else if (msg.getHeader().getMsgType() == MsgType.DELIVERED) {
            String sessionId = msg.getBody().getSessionId();
            String tempMsgId = msg.getBody().getTempMsgId();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到sessionId：【{}】中，tempMsgId是：{} 消息的DELIVERED（已发送）消息，服务端分配的msgId是：{}", sessionId, tempMsgId, msgId);
        }
        else if (msg.getHeader().getMsgType() == MsgType.SENDER_SYNC) {
            String fromClient = msg.getBody().getFromClient();
            String content = msg.getBody().getContent();
            long msgId = msg.getBody().getMsgId();
            log.info("<<===收到本账号其它在线设备【{}】发过来的的SENDER_SYNC消息，消息内容是：{}，sessionId是：{}，msgId是：{}", fromClient, content, msg.getBody().getSessionId(), msgId);
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
