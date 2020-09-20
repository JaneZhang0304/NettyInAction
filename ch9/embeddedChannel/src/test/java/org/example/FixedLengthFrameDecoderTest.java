package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import org.example.handler.FixedLengthFrameDecoder;
import org.junit.Assert;
import org.junit.Test;

public class FixedLengthFrameDecoderTest {
    @Test
    public void testFramesDecoded(){
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        System.out.println("buf refCnt: "+buf.refCnt());
        ByteBuf input = buf.duplicate();
        System.out.println("ByteBuf origin writerIndex: "+buf.writerIndex());
        System.out.println("ByteBuf duplicate writerIndex: "+input.writerIndex());

        System.out.println("buf refCnt: "+buf.refCnt());

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));
        //write bytes
        System.out.println("ByteBuf refCount: "+input.refCnt());
        ByteBuf retainBuf = input.retain();//这个方法只是会把refCount+1
        System.out.println("ByteBuf refCnt: "+retainBuf.refCnt());
        System.out.println(retainBuf==input);
        boolean result1 = channel.writeInbound(retainBuf);
        Assert.assertTrue(result1);
        Assert.assertTrue(channel.finish());//Any further try to write data to it will fail.
        //read messages
        ByteBuf read = (ByteBuf) channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);//前三个字节
        System.out.println(read.toString(CharsetUtil.UTF_8));
        read.release();
        read = (ByteBuf) channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);//前三个字节
        System.out.println(read.toString(CharsetUtil.UTF_8));
        read.release();
        read = (ByteBuf) channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);//前三个字节
        System.out.println(read.toString(CharsetUtil.UTF_8));
        read.release();

        Assert.assertNull(channel.readInbound());
        System.out.println("buf refCnt: "+buf.refCnt());
        buf.release();
        System.out.println("buf refCnt: "+buf.refCnt());
    }

    @Test
    public void testFrameDecoder2(){
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3)
        );
//        Assert.assertTrue(channel.writeInbound(input.readBytes(4)));
//        Assert.assertTrue(channel.writeInbound(input.readBytes(5)));

        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(5)));

        Assert.assertTrue(channel.finish());

        ByteBuf read=(ByteBuf)channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);
        read.release();

        read=(ByteBuf)channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);
        read.release();

        read=(ByteBuf)channel.readInbound();
        Assert.assertEquals(buf.readSlice(3),read);
        read.release();

        Assert.assertNull(channel.readInbound());
        buf.release();

    }
}
