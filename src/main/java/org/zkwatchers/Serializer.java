package org.zkwatchers;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.StandardCharsets;

public class Serializer implements ZkSerializer {
    @Override
    public byte[] serialize(Object o) throws ZkMarshallingError {
        //we'll replace this with serialization of our packets in the future
        return ((String) o).getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        //we'll replace this with a deserialization of our packets using a switch case or something
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
