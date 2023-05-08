package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

public class PlayerCount extends ZookeeperData{
    private Integer count;

    public PlayerCount(){
        count = 0;
    }

    public PlayerCount(int count){
        this.count = count;
    }

    public PlayerCount increment(){
        count++;
        return this;
    }

    public int getCount(){
        return count;
    }

    @Override
    public byte[] serialize() {
        return Bytes.concat(new byte[]{Integer.valueOf(6).byteValue(), Integer.valueOf(0).byteValue()}, new byte[]{count.byteValue()});
    }
}
