package com.hibob.anyim.client.client;

import com.hibob.anyim.client.consts.Const;
import com.hibob.anyim.client.handler.ByteBufToWebSocketFrame;
import com.hibob.anyim.client.handler.ClientHandler;
import com.hibob.anyim.client.handler.WebSocketToByteBufEncoder;
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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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

    private static NioEventLoopGroup group;
    private static Bootstrap bootstrap;
    private static Scanner scanner = new Scanner(System.in);

    public static void start(UserClient userClient, String token, String nettyPort) throws URISyntaxException, InterruptedException {
        log.info("===>NettyClient start......");
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        URI uri = new URI("ws://localhost:" + nettyPort + "/ws");
        DefaultHttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaderNames.AUTHORIZATION, token);
        headers.add("account", userClient.getAccount());
        headers.add("clientId", userClient.getClientId());
        try {
            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
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

            // 这里只处理发送出去的消息
            while (true) {
                String line = scanner.nextLine();
                if (!channelFuture.channel().isActive()) {
                    break;
                }

                if ("exit".equals(line)) {
                    break;
                }
                // 根据line中"[user01]"符号，解析出要发送的对方账号user01
                String toId = "";
                String content = "";
                try {
                    toId = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                    // 然后解析出后面要发送的内容
                    content = line.substring(line.indexOf("]") + 1);
                }
                catch (Exception e) {
                    log.info("===>输入非法");
                    continue;
                }

                Header header = Header.newBuilder()
                        .setMagic(Const.MAGIC)
                        .setVersion(0)
                        .setMsgType(MsgType.CHAT)
                        .setIsExtension(false)
                        .build();
                Body body = Body.newBuilder()
                        .setFromId(userClient.getAccount())
                        .setFromClient(userClient.getClientId())
                        .setToId(toId)
                        .setSeq(1)
                        .setAck(1)
                        .setContent(content)
                        .setTempMsgId(UUID.randomUUID().toString())
                        .build();
                Msg msg = Msg.newBuilder().setHeader(header).setBody(body).build();
                channelFuture.channel().writeAndFlush(msg);
                log.info("===>发给[{}]的消息：{}", toId, content);
            }

            log.info("===>（1）等待5秒开始重连");
            Thread.sleep(5000);
            NettyClient.start(userClient, token, nettyPort);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            log.info("===>（2）等待5秒开始重连");
            Thread.sleep(5000);
            NettyClient.start(userClient, token, nettyPort);
        }
        finally {
            group.shutdownGracefully();
        }

    }
}
