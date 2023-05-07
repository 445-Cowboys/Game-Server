package org.server;

import org.server.packets.Factory;
import org.zk.dataClasses.GameRoomsInfo;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

//This is going to take a packet, run it through a switch case that will
//decode the packet into a usable class object where different things will
//happen depending on the class that gets generated
public class RequestHandler implements Runnable{
    ByteBuffer data;
    SocketAddress client;
    DatagramChannel channel = DatagramChannel.open().bind(null);
    public RequestHandler(SocketAddress client, ByteBuffer data) throws IOException {
        this.client = client;
        this.data = data;
    }

    /**
     * decode the byte buffer, do some read or write action, send an ack back to the original client.
     */
    @Override
    public void run() {
        try {
            packetHandler();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles packet info depending on the opcode found in the front
     * @throws IOException
     */
    public void packetHandler() throws IOException {
        //arbitrarily picked 500
        ByteBuffer ackPacket = ByteBuffer.allocate(500);
        switch ((int) data.get(0)){
            case 20:
                System.out.println("Received an initial awake connection from " + client);
                GameRoomsInfo gfInfo = Main.zkClient.getGameRoomsInfo();
                //add the ip address of the client to the list of clients in the lobby room
                Main.zkClient.addPlayerToLobby(client.toString().split(":")[0]);
                //TODO increment client count here
                channel.send(new Factory().makeGameRooms(new int[]{gfInfo.getGameRoom(0).getSize(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new boolean[]{gfInfo.getGameRoom(0).getRoomIsFull(), gfInfo.getGameRoom(1).getRoomIsFull(), gfInfo.getGameRoom(2).getRoomIsFull()},new int[]{gfInfo.getGameRoom(0).getState(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new int[]{Main.zkClient.checkServerStatus("moxie.cs.oswego.edu"),Main.zkClient.checkServerStatus("rho.cs.oswego.edu"),Main.zkClient.checkServerStatus("pi.cs.oswego.edu")}), client);
                break;
            //Game action packet
            case 1:
                //send an ACK back
                //TODO modify game state based on the action
                break;
            //Exit packet
            case 2:
                //send an ack back
                //remove the IP from the list of clients
                break;
            //Enter game room packet
            case 3:
                //send an ack back
                //if the game is currently not in session & there are less than three people, allow access.
                //otherwise deny entry
                break;
            default:
                break;
        }
    }
}