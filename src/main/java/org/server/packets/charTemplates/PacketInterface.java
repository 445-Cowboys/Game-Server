package org.server.packets.charTemplates;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Random;

public class PacketInterface {

    DatagramSocket socket;
    ServerSocket sSocket;
    public PacketInterface() throws SocketException {
        socket = new DatagramSocket();

    }

    public PacketInterface(int port, String host) throws IOException {
        socket = new DatagramSocket(port, InetAddress.getByName(host));
        System.out.println("Server listening on port :" + port);

    }
    public PacketInterface(int port) throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("Server listening on port :" + port);
    }

    /** This method calls the receive method with an infinite timeout **/
  /*  public PacketFactory receive() throws IOException {
        return receive(0);
    }

    public PacketFactory receive(int timeout) throws SocketTimeoutException, IOException {
        //create a new DatagramPacket to hold the data
        DatagramPacket dpacket = new DatagramPacket(new byte[PacketFactory.DEFAULT_PACKET_SIZE],
                PacketFactory.DEFAULT_PACKET_SIZE);

        //set the timeout
        socket.setSoTimeout(timeout);

        try {
            //get the datagram packet from the socket
            socket.receive(dpacket);
        } catch (SocketTimeoutException e) {
            return null;
        }

        //set the timeout back to 0 (inf)
        socket.setSoTimeout(0);

        //using the factory method create the new TFTPPacket and return it
        return PacketFactory.packetFactory(dpacket);

    } */

    public boolean send(PacketFactory packet, boolean drops) throws IOException {
        //to simulate a dropped packet we will simply 1% of the time discard the packet
        boolean dropped = false;
        Random r = new Random();
        int num = 50;
        int random;

        if(drops){
                random = r.nextInt(100);
                if (num == random) {
                    System.out.println("Packet dropped on send");
                    dropped = true;
                }
                else {
                    socket.send(packet.getDatagramPacket()); }
        }

        else {
            socket.send(packet.getDatagramPacket());
        }

        return dropped;
    }

    public void destroy() {
        socket.close();
    }
    public DatagramSocket getDataSocket(){
        return socket;
    }
    public ServerSocket getServerSocket(){
        return sSocket;
    }


}
