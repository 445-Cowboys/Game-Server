package org.server;

import org.server.packets.packets.Factory;
import org.server.packets.packets.GameRooms;
import org.zk.dataClasses.GameRoom;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class TestClient {
    public static void main(String[] args) throws IOException {
        //for loop that always listens for input, this test client mimicks
        //our client that will send packets to the server.
        //look at the switch case to see what each given value means
        //make our channel that we'll send stuff to.
        DatagramChannel channel = DatagramChannel.open().bind(null);
        Scanner scanner = new Scanner(System.in);
        for(;;){
            System.out.println("Give packet type to send 1-9");
            String input = scanner.next();
            switch (input){
                case "20":
                    //20 is initial login packet
                    System.out.println("Awake Packet");
                    ByteBuffer buf = ByteBuffer.allocate(1);
                    buf.put((byte) 20);
                    buf.flip();
                    GameRooms gr = new GameRooms(sendPacket(buf, channel));
                    System.out.println(gr.getOpcode());
                    break;

                default:
                    System.out.println("Error in input");
                    break;
            }
        }
    }


    public static ByteBuffer sendPacket(ByteBuffer packet, DatagramChannel channel) throws IOException {
        //send packet to server
        channel.send(packet, new InetSocketAddress("moxie.cs.oswego.edu", 7086));
        ByteBuffer receivedBuf = ByteBuffer.allocate(1024);
        channel.receive(receivedBuf);
        //do nothing with the ack,we don't really care what it says rn we are more worried about how the server
        //responds to packets that get sent to it.
        System.out.println("ACK received");
        receivedBuf.flip();
        return receivedBuf;
    }


}
