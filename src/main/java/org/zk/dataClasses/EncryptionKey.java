package org.zk.dataClasses;

import com.google.common.primitives.Bytes;
import com.google.crypto.tink.KeysetHandle;
import org.server.AEAD;

import java.math.BigInteger;

public class EncryptionKey extends ZookeeperData{
    private AEAD publicKey;

    EncryptionKey(AEAD publicKey){
        this.publicKey = publicKey;
    }

    public AEAD getPublicKey() {
        return publicKey;
    }

    @Override
    public byte[] serialize() {
        byte[] aeadBytes = new byte[0];
        try {
            aeadBytes = publicKey.getKeySetAsJSON();
        }catch (Exception e){
            e.printStackTrace();
        }
        return Bytes.concat(new byte[]{Integer.valueOf(2).byteValue(), Integer.valueOf(0).byteValue()}, aeadBytes);
    }
}
