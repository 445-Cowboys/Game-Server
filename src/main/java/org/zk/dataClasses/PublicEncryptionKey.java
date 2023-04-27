package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.math.BigInteger;

public class PublicEncryptionKey extends ZookeeperData{
    private BigInteger publicKey;

    PublicEncryptionKey(BigInteger publicKey){
        this.publicKey = publicKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] serialize() {
        return Bytes.concat(new byte[]{Integer.valueOf(2).byteValue(), Integer.valueOf(0).byteValue()}, publicKey.toByteArray());
    }
}
