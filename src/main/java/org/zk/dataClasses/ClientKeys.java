package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;

public class ClientKeys extends ZookeeperData{
    private ArrayList<BigInteger> clientKeys;
    public ClientKeys(){}

    public ClientKeys(ArrayList<BigInteger> clientKeys){
        this.clientKeys = clientKeys;
    }

    public void addClientKey(BigInteger clientKey){
        clientKeys.add(clientKey);
    }

    public ArrayList<BigInteger> getClientKeys() {
        return clientKeys;
    }

    @Override
    public byte[] serialize() {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         try {
             ObjectOutputStream oos = new ObjectOutputStream(bos);
             oos.writeObject(clientKeys);
         }catch (IOException e){
             e.printStackTrace();
         }
        return Bytes.concat(new byte[]{Integer.valueOf(3).byteValue(), Integer.valueOf(0).byteValue()}, bos.toByteArray());
    }

}
