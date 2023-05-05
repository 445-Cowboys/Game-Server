package org.zk.dataClasses;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Clients extends ZookeeperData{
    private ArrayList<String> Clients = new ArrayList<>();

    public Clients(){}

    public Clients(ArrayList<String> waitingClients){this.Clients=waitingClients;}

    @Override
    public byte[] serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(Clients);
        }catch (IOException e){
            e.printStackTrace();
        }
        return Bytes.concat(new byte[]{Integer.valueOf(6).byteValue(), Integer.valueOf(0).byteValue()}, bos.toByteArray());
    }

    public void addClient(String clientIP){
        Clients.add(clientIP);
    }

    public void removeClient(String clientIP){
        Clients.remove(clientIP);
    }

    public ArrayList<String> getWaitingClients() {
        return Clients;
    }
}
