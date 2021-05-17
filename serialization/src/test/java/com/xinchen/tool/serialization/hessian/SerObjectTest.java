package com.xinchen.tool.serialization.hessian;


import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * @author xin chen
 * @version 1.0.0
 * @date 2021/3/3 13:25
 */
class SerObjectTest {
    private static final SerObject obj = new SerObject(1L, "Test", new ArrayList<>());

    @Test
    void jdk_serialization() throws IOException, ClassNotFoundException {
        // serialization
        byte[] data;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)){
            oos.writeObject(obj);
        } finally {
            data = bos.toByteArray();
        }
        System.out.format("Jdk serialization size: %d%n", data.length);

        // un_serialization
        Object o;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()))){
          o = ois.readObject();
        }
        System.out.format("Jdk serialization content: %s%n", o);
    }

    @Test
    void hessian_serialization() throws IOException {
        // serialization
        byte[] data;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(bos);
        output.writeObject(obj);
        data = bos.toByteArray();
        System.out.format("Hessian serialization size: %d%n", data.length);

        // un_serialization
        Object o;
        HessianInput input = new HessianInput(new ByteArrayInputStream(data));
        o = input.readObject();
        System.out.format("Hessian serialization content: %s%n", o);
    }

    @Test
    void hessian2_serialization() throws IOException {
        // serialization
        byte[] data;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(bos);
        output.writeObject(obj);
        output.getBytesOutputStream().flush();
//        output.completeMessage();
        output.close();
        data = bos.toByteArray();
        System.out.format("Hessian2 serialization size: %d%n", data.length);

        // un_serialization
        Object o;
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        o = input.readObject();
        System.out.format("Hessian2 serialization content: %s%n", o);
    }
}