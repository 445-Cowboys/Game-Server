package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

public class ServerData extends ZookeeperData{

    private final String address;

    public ServerData(String address){
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    @Override
    public byte[] serialize() {
        return Bytes.concat(new byte[]{Integer.valueOf(1).byteValue(), Integer.valueOf(0).byteValue()}, address.getBytes());
    }
}
