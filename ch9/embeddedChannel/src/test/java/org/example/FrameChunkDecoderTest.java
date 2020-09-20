package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.CharsetUtil;
import org.example.handler.FrameChunkDecoder;
import org.junit.Assert;
import org.junit.Test;

public class FrameChunkDecoderTest {
    @Test
    public void testFrameDecoded(){
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel=new EmbeddedChannel(new FrameChunkDecoder(3));
        Assert.assertTrue(channel.writeInbound(input.readBytes(2)));
        try{
            channel.writeInbound(input.readBytes(4));
            Assert.fail();//如果上面没有抛出异常则会到达这里，表示测试失败
        }catch (TooLongFrameException e){
            System.out.println("Expect exception: "+e.getMessage());
        }
        Assert.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assert.assertTrue(channel.finish());

        //Read frames
        ByteBuf read = (ByteBuf)channel.readInbound();
        Assert.assertEquals(buf.readSlice(2),read);
        read.release();
        read = (ByteBuf)channel.readInbound();
        Assert.assertEquals(buf.skipBytes(4).readSlice(3),read);
        System.out.println(read.toString(CharsetUtil.UTF_8));
        read.release();
        buf.release();
    }
}
