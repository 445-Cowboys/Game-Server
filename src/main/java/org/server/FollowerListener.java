package org.server;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

//A follower listener is spawned by a leader to listen for incoming receptions from it's follwers
//this is so that the traffic doesn't get too backed up by clients and follower transmissions
//potentially all being sent over the same channel
public class FollowerListener implements Runnable{

    DatagramChannel channel;

    public FollowerListener(DatagramChannel channel){
        this.channel=channel;
    }

    @Override
    public void run() {
        //this will essentially just run and keep listening until the leader crashes.
        //it is the same loop as the Main class loop, only it listens on a specific different port
        for(;;){
            //listen for messages sent to this server, pass along the message to a request handler
            ByteBuffer data = ByteBuffer.allocate(1024);
            try {
                new Thread(new RequestHandler(channel.receive(data), data)).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
