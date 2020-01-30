package hdli.chapter7;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class EchoClient {

    private final String host;
    private final int port;
    private final int sendNumber;


    public EchoClient(String host, int port, int sendNumber) {
        this.host = host;
        this.port = port;
        this.sendNumber = sendNumber;
    }

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChildChannelHandler());

            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
            ch.pipeline().addLast("msgpack decoder", new MsgPackDecoder());
            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
            ch.pipeline().addLast("msgpack encoder", new MsgPackEncoder());
            ch.pipeline().addLast(new EchoClientHandler(sendNumber));
        }
    }

    private class EchoClientHandler extends ChannelHandlerAdapter {

        private final int sendNumber;

        public EchoClientHandler(int sendNumber) {
            this.sendNumber = sendNumber;
        }

        public void channelActive(ChannelHandlerContext ctx) {
            UserInfo[] infos = createUserInfos();
            for (UserInfo userInfo : infos) {
                System.out.println(userInfo);
                ctx.write(userInfo);
            }
            ctx.flush();
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            System.out.println("client receive the msgpack message : " + msg);
            ctx.write(msg);
        }

        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        private UserInfo[] createUserInfos() {
            UserInfo[] userInfos = new UserInfo[sendNumber];
            UserInfo userInfo = null;
            for (int i = 0; i < sendNumber; i++) {
                userInfo = new UserInfo(i, "ABCDEFG--->" + i);
                userInfos[i] = userInfo;
            }
            return userInfos;
        }

    }

    public static void main(String[] args) {
        new EchoClient("127.0.0.1", 8080, 11).connect();
    }
}
