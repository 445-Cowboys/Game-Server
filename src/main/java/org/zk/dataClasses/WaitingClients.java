package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class WaitingClients extends ZookeeperData{
    private ArrayList<String> waitingClients = new ArrayList<>();

    public WaitingClients(){}

    public WaitingClients(ArrayList<String> waitingClients){this.waitingClients=waitingClients;}

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(waitingClients);
        }catch (IOException e){
            e.printStackTrace();
        }
        return Bytes.concat(new byte[]{Integer.valueOf(6).byteValue(), Integer.valueOf(0).byteValue()}, bos.toByteArray());
    }

    public void addClient(String clientIP){
        waitingClients.add(clientIP);
    }

    public void removeClient(String clientIP){
        waitingClients.remove(clientIP);
    }

    public ArrayList<String> getWaitingClients() {
        return waitingClients;
    }
}
