package org.zk.watchers;

import java.io.IOException;
import java.nio.channels.DatagramChannel;

public class HeartBeatThread implements Runnable{
    DatagramChannel channel = DatagramChannel.open().bind(null);

    public HeartBeatThread() throws IOException {
    }

    @Override
    public void run() {
        //send the heartbeat, listen for an ack from the client. If we don't get
        //a response from the client we remove their ip from list of live clients
        //before a run of each check, we make sure the ip is still in the list of clients and that they didn't gracefully
        //exit. If they gracefully exited, stop the thread.

        //max number of retries is 10
        int curAttempt = 0;
        while(curAttempt < 10){
            //make the packet to send
            //send it over to the client and await an ack. If the returned value is null then we did not get an ack
            //and we will try again so long as the curAttempt < 10



            curAttempt++;
        }


        //if we get here then the number of attempts limit was reached. We do one final check for the client's ip with
        //a write lock and get rid of them from the list of live clients.
    }
}
