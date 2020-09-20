package org.example;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.example.handler.AbsIntegerEncoder;
import org.junit.Assert;
import org.junit.Test;

public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded(){
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 10; i++) {
            buf.writeInt(i*-1);
        }
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assert.assertTrue(channel.writeOutbound(buf));
        Assert.assertTrue(channel.finish());
        //read bytes
        for (int i = 0; i < 10; i++) {
            int out = channel.readOutbound();
            System.out.println(out);
            Assert.assertEquals(i,out);
        }
        Assert.assertNull(channel.readOutbound());
    }
}
