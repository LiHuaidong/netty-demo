package hdli.chapter7;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

    public EchoServerHandler() {
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        UserInfo userInfo = (UserInfo) msg;
        System.out.println("client receive the msgpack message : " + userInfo);

        ctx.writeAndFlush(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
