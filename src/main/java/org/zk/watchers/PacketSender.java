package org.zk.watchers;

import org.server.Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;

public class PacketSender implements Runnable{
    private String clientAddress;
    private int ackNum;
    private ByteBuffer buffer;

    private String path;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private DatagramChannel channel;

    public PacketSender(String clientAddress, int ackNum, String path, ByteBuffer buffer){
        this.clientAddress=clientAddress;
        this.ackNum=ackNum;
        this.path = path;
        this.buffer = buffer;
        try {
            channel = DatagramChannel.open().bind(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        ByteBuffer ackBuf = ByteBuffer.allocate(1);
        //our task we will time
        Callable<Void> Callable = () -> {
            try {
                channel.receive(ackBuf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        };
        try {
            //if the client is dead, the path used to call it up will eventually die, just keep retrying until that happens
            int retryNum = 0;
            while(Main.zkClient.pathExists(path) && retryNum < 10) {
                //make sure the position is at 0.
                buffer.position(0);
                channel.send(buffer, new InetSocketAddress(clientAddress.split(":")[0], Integer.parseInt(clientAddress.split(":")[1])));
                Future<Void> task = executorService.submit(Callable);
                //and now the channel waits to receive word back from the client
                try{
                    task.get(2500, TimeUnit.MILLISECONDS);
                }catch (TimeoutException | InterruptedException | ExecutionException e){
                    //we didn't get an ack back in time, increment the retry counter and continue
                    System.out.println("timed out...");
                    System.out.println("retrying to send to "+clientAddress);
                    channel.close();
                    channel = DatagramChannel.open().bind(null);
                    buffer.rewind();
                    retryNum++;
                    if(retryNum == 10)
                        break;
                    continue;
                }
                if ((int) ackBuf.get(0) == ackNum) {
                    channel.close();
                    return;
                }
                //if we get here we somehow got a value from somewhere else so just retry without incrementing the retry num
                channel.close();
                channel = DatagramChannel.open().bind(null);
                buffer.rewind();
                retryNum++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
