package org.server;

import org.server.packets.packets.Factory;
import org.zk.dataClasses.GameRoomsInfo;
import org.zk.dataClasses.GameState;

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
        switch ((int) data.get(0)){
            case 20:
                System.out.println("Received an initial awake connection from " + client);
                GameRoomsInfo gfInfo = Main.zkClient.getGameRoomsInfo();
                //add the ip address of the client to the list of clients in the lobby room
                Main.zkClient.addPlayerToLobby(client.toString().split(":")[0]);
                channel.send(new Factory().makeGameRooms(new int[]{gfInfo.getGameRoom(0).getSize(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new boolean[]{gfInfo.getGameRoom(0).getRoomIsFull(), gfInfo.getGameRoom(1).getRoomIsFull(), gfInfo.getGameRoom(2).getRoomIsFull()},new int[]{gfInfo.getGameRoom(0).getState(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new int[]{Main.zkClient.checkServerStatus("moxie.cs.oswego.edu"),Main.zkClient.checkServerStatus("rho.cs.oswego.edu"),Main.zkClient.checkServerStatus("pi.cs.oswego.edu")}), client);
                break;
            default:
                break;
        }

        // temporary until full packet handling is implemented
        GameState gs = Main.zkClient.getGameState(0);

        // pretend we got an attack packet from the client
        gs.playerAttack();

        if (gs.getCurrentPlayer() == gs.getPlayers().size() - 1) { // if the villain is next
            gs.villainAttack();
        }

        // pretend we got a defend packet from the client
        gs.defend();

        // pretend we got a reload packet from the client
        gs.reload();
    }
}
