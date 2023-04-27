package org.zk.watchers;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.zk.dataClasses.ZookeeperData;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Serializer implements ZkSerializer {
    @Override
    public byte[] serialize(Object o) throws ZkMarshallingError {
        return ((ZookeeperData) o).serialize();
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        try {
            return ZookeeperData.deserialize(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
