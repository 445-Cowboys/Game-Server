package org.zk.dataClasses;

import com.google.common.primitives.Bytes;
import org.server.AEAD;

import java.math.BigInteger;

public class EncryptionKey extends ZookeeperData{
    private byte[] publicKey;

    public EncryptionKey(byte[] publicKey){
        this.publicKey = publicKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] serialize() {
        return Bytes.concat(new byte[]{Integer.valueOf(2).byteValue(), Integer.valueOf(0).byteValue()}, publicKey);
    }
}
