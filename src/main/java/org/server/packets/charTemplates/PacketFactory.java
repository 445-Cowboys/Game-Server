package org.server.packets.charTemplates;

import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class PacketFactory {


    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int PLAYER_3 = 3;
    public static final int BOSS = 4;

    public static final int ACKNOWLEDGEMENT = 5;

    public static final int ERROR = 6;

    /** The type of packet **/
    private int type;

    /** The port for the packet **/
    private int port;

    /** The address for the packet **/
    private InetAddress address;

    public PacketFactory(InetAddress address, int port, int type){
        this.type = type;
        this.port = port;
        this.address = address;
    }

    public PacketFactory(DatagramPacket packet, int type){
        this.type = type;
        this.port = packet.getPort();
        this.address = packet.getAddress();
    }

    public final static PacketFactory packetFactory(DatagramPacket packet){
        PacketFactory tmpPF = null;

        //get the data out of the packet
        byte[] data = packet.getData();

        //get the type
        byte type = data[1];
        if(type == PLAYER_1 || type == PLAYER_2 || type == PLAYER_3){
            tmpPF = new Hero(packet, type);
        }else if(type == BOSS){
            //tmpPF = new Villain(packet, type);
        }else if(type == ACKNOWLEDGEMENT){
            //tmpPF = new AckPacket(packet, type);
        }else if(type == ERROR){
            //tmpPF = new ErrorPacket(packet, type);
        }


        return tmpPF;
    }

    /**
     * This method will create a new DatagramPacket from itself (E)
     * @return DatagramPacket
     */
    public abstract DatagramPacket getDatagramPacket();

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

}
