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
     */
    @Override
    public void run() {
        packetHandler();
    }


    /**
     * I only put the packet handling in a different method in case I end up needing to modify the run function
     */
    public static void packetHandler(){
        System.out.println("we're here");


    }
}
