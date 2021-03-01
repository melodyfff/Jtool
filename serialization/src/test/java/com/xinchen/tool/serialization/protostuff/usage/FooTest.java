package com.xinchen.tool.serialization.protostuff.usage;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/1 14:52
 */
class FooTest {

    @Test
    void roundTrip(){
        Foo foo = new Foo("foo", 1);

        // this is lazily created and cached by RuntimeSchema
        // so its safe to call RuntimeSchema.getSchema(Foo.class) over and over
        // The getSchema method is also thread-safe
        Schema<Foo> schema = RuntimeSchema.getSchema(Foo.class);

        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        // ser
        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(foo, schema, buffer);
        }
        finally {
            buffer.clear();
        }

        // deser
        Foo fooParsed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, fooParsed, schema);

        assertEquals(fooParsed.getId(), foo.getId());
        assertEquals(fooParsed.getName(), foo.getName());
    }
}