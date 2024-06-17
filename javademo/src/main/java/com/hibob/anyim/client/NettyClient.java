package com.hibob.anyim.client;

import com.hibob.anyim.consts.Const;
import com.hibob.anyim.entity.User;
import com.hibob.anyim.handler.ByteBufToWebSocketFrame;
import com.hibob.anyim.handler.ClientHandler;
import com.hibob.anyim.handler.WebSocketToByteBufEncoder;
import com.hibob.anyim.netty.protobuf.Body;
import com.hibob.anyim.netty.protobuf.Header;
import com.hibob.anyim.netty.protobuf.Msg;
import com.hibob.anyim.netty.protobuf.MsgType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.UUID;


/**
 * 启动测试前要先启动User服务
 */
@Slf4j
public class NettyClient {

    private static User user;
    private static String nettyPort = "80";
    private static NioEventLoopGroup group;
    private static Bootstrap bootstrap;
    private static Channel channel;

    public static void setUser(User user) {
        NettyClient.user = user;
    }

    public static void setNettyPort(String nettyPort) {
        NettyClient.nettyPort = nettyPort;
    }

    public static void start() throws URISyntaxException {
        log.info("===>NettyClient start......");
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        URI uri = new URI("ws://localhost:" + nettyPort + "/ws");
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaderNames.AUTHORIZATION, user.getAccessToken());
        headers.add("account", user.getAccount());
        headers.add("clientId", user.getClientId());
        try {
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new HttpObjectAggregator(65536));
                            pipeline.addLast(new WebSocketServerCompressionHandler()); // WebSocket数据压缩
                            pipeline.addLast(new WebSocketClientProtocolHandler(
                                    WebSocketClientHandshakerFactory.newHandshaker(
                                            uri,
                                            WebSocketVersion.V13,
                                            (String)null,
                                            false,
                                            headers)));
                            pipeline.addLast(new WebSocketToByteBufEncoder()); //解码：WebSocketFrame -> ByteBuf
                            pipeline.addLast(new ProtobufVarint32FrameDecoder()); //解码：处理半包黏包，参数类型是ByteBuf
                            pipeline.addLast(new ProtobufDecoder(Msg.getDefaultInstance())); //解码：ByteBuf -> Msg
                            pipeline.addLast(new ByteBufToWebSocketFrame()); //编码：ByteBuf -> WebSocketFrame(二进制)
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender()); //编码：处理半包黏包，参数类型是ByteBuf
                            pipeline.addLast(new ProtobufEncoder()); //编码：Msg -> ByteBuf
                            pipeline.addLast(new ClientHandler()); // 业务处理理器，读写都是Msg
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            channel = channelFuture.channel();
            startDaemon();
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void reconnect() throws URISyntaxException, InterruptedException {
        URI uri = new URI("ws://localhost:" + nettyPort + "/ws");
        ChannelFuture channelFuture = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
        channel = channelFuture.channel();
    }

    public static void stop() {
        group.shutdownGracefully();
    }

    public static void scannerInChat() throws URISyntaxException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("exit".equals(line)) {
                log.info("===>会话结束");
                stop();
                break;
            }

            // 根据line中"[user01]"符号，解析出要发送的对方账号user01
            String toId = "";
            String content = "";
            try {
                toId = line.substring(line.indexOf("@") + 1, line.indexOf(" "));
                content = line.substring(line.indexOf(" ") + 1);

            }
            catch (Exception e) {
                log.info("===>输入非法");
            }

            if (!channel.isActive()) {
                start();
            }
            sendChat(toId, content);
        }
    }

    public static void scannerInGroupChat() throws URISyntaxException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if ("exit".equals(line)) {
                log.info("===>会话结束");
                stop();
                break;
            }

            // 根据line中"[user01]"符号，解析出要发送的对方账号user01
            String to = "";
            String content = "";
            try {
                to = line.substring(line.indexOf("@") + 1, line.indexOf(" "));
                content = line.substring(line.indexOf(" ") + 1);

            }
            catch (Exception e) {
                log.info("===>输入非法");
            }

            if (!channel.isActive()) {
                start();
            }
            sendGroupChat(Long.parseLong(to), content);
        }
    }

    public static void send(MsgType msgType, String to, String content) throws InterruptedException {
        switch (msgType) {
            case CHAT:
                sendChat(to, content);
                break;
            case GROUP_CHAT:
                sendGroupChat(Long.parseLong(to), content);
                break;
            case READ:
//                sendRead(toId, content);
                break;
            case DELIVERED:
//                sendDelivered(toId, content);
                break;
            case SENDER_SYNC:
//                sendSenderSync(toId, content);
                break;
            default:
                break;
        }
    }

    private static void startDaemon() {
        new Thread(() -> {
            while (true) {
                if (channel.isActive()) {
                    log.info("Netty服务检测正常！");
                }
                else {
                    try {
                        log.info("Netty服务检测异常，尝试重连！");
                        reconnect();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private static void sendChat(String toId, String content) throws InterruptedException {
        Header header = Header.newBuilder()
                .setMagic(Const.MAGIC)
                .setVersion(0)
                .setMsgType(MsgType.CHAT)
                .setIsExtension(false)
                .build();
        Body body = Body.newBuilder()
                .setFromId(user.getAccount())
                .setFromClient(user.getClientId())
                .setToId(toId)
                .setSeq(1)
                .setAck(1)
                .setContent(content)
                .setTempMsgId(UUID.randomUUID().toString())
                .build();
        Msg msg = Msg.newBuilder().setHeader(header).setBody(body).build();

        // 等待握手成功
        int timeOutCnt = 30;
        while (timeOutCnt > 0) {
            Object hello = channel.attr(AttributeKey.valueOf("hello")).get();
            if (hello == null) {
                Thread.sleep(100);
                timeOutCnt--;
                continue;
            }
            log.info("===握手成功，开始发送消息");
            break;
        }
        channel.writeAndFlush(msg).addListener(future -> {
            if (future.isSuccess()) {
                log.info("===>发送成功：发给[{}]的消息：{}", toId, content);
            }
            else {
                log.info("===>发送失败：发给[{}]的消息：{}，失败原因：{}", toId, content, future.cause().getMessage());
            }
        });
    }

    private static void sendGroupChat(long groupId, String content) throws InterruptedException {
        Header header = Header.newBuilder()
                .setMagic(Const.MAGIC)
                .setVersion(0)
                .setMsgType(MsgType.GROUP_CHAT)
                .setIsExtension(false)
                .build();
        Body body = Body.newBuilder()
                .setFromId(user.getAccount())
                .setFromClient(user.getClientId())
                .setGroupId(groupId)
                .setSeq(1)
                .setAck(1)
                .setContent(content)
                .setTempMsgId(UUID.randomUUID().toString())
                .build();
        Msg msg = Msg.newBuilder().setHeader(header).setBody(body).build();

        // 等待握手成功
        int timeOutCnt = 30;
        while (timeOutCnt > 0) {
            Object hello = channel.attr(AttributeKey.valueOf("hello")).get();
            if (hello == null) {
                Thread.sleep(100);
                timeOutCnt--;
                continue;
            }
            log.info("===握手成功，开始发送消息");
            break;
        }
        channel.writeAndFlush(msg).addListener(future -> {
            if (future.isSuccess()) {
                log.info("===>发送成功：发给[{}]的消息：{}", groupId, content);
            }
            else {
                log.info("===>发送失败：发给[{}]的消息：{}，失败原因：{}", groupId, content, future.cause().getMessage());
            }
        });
    }

}
