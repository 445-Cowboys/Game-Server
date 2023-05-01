package org.server;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

//This is going to take a packet, run it through a switch case that will
//decode the packet into a usable class object where different things will
//happen depending on the class that gets generated
public class RequestHandler implements Runnable{
    ByteBuffer data;
    SocketAddress client;
    public RequestHandler(SocketAddress client, ByteBuffer data){
        this.client = client;
        this.data = data;
    }


    /**
     * decode the byte buffer, do some read or write action, send an ack back to the original client.
     * If the packet is a follower retransmission, we immediately send them an ack to say we got the packet
     * and will handle the interactions from here
     */
    @Override
    public void run() {

        //if the current server is not the leader, forward the data over to the leader
        while(!Main.zkClient.getIsLeader()){
            //send to server

            //wait for ack, if the ack is never received it means the leader server is probably down.
            //but we can just keep having this server try to retransmit the packet to the leader
            //because either a new leader will be elected or this server will become the leader
            //and will break out of the while loop

            //wait for a future that will hold the ack packet, give it 5 seconds max to receive the packet


            return;
        }
        //if we get to this point, it means we are the leader call the static method that will handle the packets and
        packetHandler();
    }


    public static void packetHandler(){

    }
}
