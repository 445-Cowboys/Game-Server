package org.server;

import org.server.packets.EnterRoom;
import org.server.packets.Factory;
import org.server.packets.PlayerAction;
import org.zk.dataClasses.EncryptionKey;
import org.zk.dataClasses.GameRoomsInfo;
import org.zk.dataClasses.GameState;
import org.zk.dataClasses.PlayerCount;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.security.GeneralSecurityException;
import java.util.Arrays;

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
//            System.out.println("Client IP is "+client.toString());
            //first check if the packet comes from one of the game rooms. If it does, decrypt it.
            for(String client:Main.zkClient.getGameClients(0)){
                if(client.contains("read-lock")||client.contains("write-lock")) continue;
                if(client.contains(this.client.toString().split(":")[0].substring(1))){
                    //decrypt the packet, send it to the packet handler, and then return
                    Main.keyRoom0.parseKey(Main.zkClient.getEncryptionKey(0).getPublicKey());
                    data = ByteBuffer.wrap(Main.keyRoom0.decrypt(Arrays.copyOfRange(data.array(), 0, data.position())));
                    gamePacketHandler(data);
                    return;
                }
            }
            for(String client:Main.zkClient.getGameClients(1)){
                if(client.contains("read-lock")||client.contains("write-lock")) continue;
                if(client.contains(this.client.toString().split(":")[0].substring(1))){
                    //decrypt the packet, send it to the packet handler, and then return
                    Main.keyRoom1.parseKey(Main.zkClient.getEncryptionKey(1).getPublicKey());
                    data = ByteBuffer.wrap(Main.keyRoom1.decrypt(Arrays.copyOfRange(data.array(), 0, data.position())));
                    //maybe need to flip? I don't think so though
                    gamePacketHandler(data);
                    return;
                }
            }
            for(String client:Main.zkClient.getGameClients(2)){
                if(client.contains("read-lock")||client.contains("write-lock")) continue;
                if(client.contains(this.client.toString().split(":")[0].substring(1))){
                    //decrypt the packet, send it to the packet handler, and then return
                    Main.keyRoom2.parseKey(Main.zkClient.getEncryptionKey(2).getPublicKey());
                    data = ByteBuffer.wrap(Main.keyRoom0.decrypt(Arrays.copyOfRange(data.array(), 0, data.position())));
                    gamePacketHandler(data);
                    return;
                }
            }

            //if it isn't in any of the game rooms, the client is new or in the lobby, call the packet handler as normal
            packetHandler();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles packet info depending on the opcode found in the front
     * @throws IOException
     */
    public void packetHandler() throws IOException, InterruptedException {
        //arbitrarily picked 500
        ByteBuffer ackPacket = ByteBuffer.allocate(500);
        int port_num;
        switch ((int) data.get(0)){
            case 20:
//                System.out.println("Received an initial awake connection from " + client);
                GameRoomsInfo gfInfo = Main.zkClient.getGameRoomsInfo();
                port_num = data.getInt(1);
                channel.send(new Factory().makeGameRooms(new int[]{gfInfo.getGameRoom(0).getSize(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new boolean[]{gfInfo.getGameRoom(0).getRoomIsFull(), gfInfo.getGameRoom(1).getRoomIsFull(), gfInfo.getGameRoom(2).getRoomIsFull()},new int[]{gfInfo.getGameRoom(0).getState(),gfInfo.getGameRoom(1).getSize(),gfInfo.getGameRoom(2).getSize()}, new int[]{Main.zkClient.checkServerStatus("rho.cs.oswego.edu"),Main.zkClient.checkServerStatus("moxie.cs.oswego.edu"),Main.zkClient.checkServerStatus("altair.cs.oswego.edu")},Main.zkClient.getPlayerCount()), client);
                //sleep for a sec so we don't back traffic up too bad for the new member
                Thread.sleep(1000);
                //add the ip address of the client to the list of clients in the lobby room
                Main.zkClient.addPlayerToLobby(client.toString().split(":")[0]+":"+port_num);
                break;
                //leave request
            case -5:
                port_num = data.getInt(1);
                if(Main.zkClient.pathExists("/lobby/waiting-clients"+client.toString().split(":")[0]+":"+port_num)) {
                    Main.zkClient.removePlayerFromLobby(client.toString().split(":")[0] + ":" + port_num);
                    Main.zkClient.decrementPlayerCount();
                } else if (Main.zkClient.pathExists("/game-rooms/0/waiting-players"+client.toString().split(":")[0]+":"+port_num)) {
                    Main.zkClient.removePlayerFromWaitingGameClients(client.toString().split(":")[0] + ":" + port_num, 0);
                    String lockID = Main.zkClient.getWriteLock("/lobby/stats");
                    //go into the room number of the lobby and decrement the number
                    GameRoomsInfo gameRoomsInfo = Main.zkClient.getGameRoomsInfo();
                    gameRoomsInfo.removePlayer(0);
                    Main.zkClient.writeToGameRoomsInfo(gameRoomsInfo);
                    Main.zkClient.releaseWriteLock(lockID);
                    Main.zkClient.decrementPlayerCount();
                } else if (Main.zkClient.pathExists("/game-rooms/1/waiting-players"+client.toString().split(":")[0]+":"+port_num)) {
                    Main.zkClient.removePlayerFromWaitingGameClients(client.toString().split(":")[0] + ":" + port_num, 1);
                    String lockID = Main.zkClient.getWriteLock("/lobby/stats");
                    //go into the room number of the lobby and decrement the number
                    GameRoomsInfo gameRoomsInfo = Main.zkClient.getGameRoomsInfo();
                    gameRoomsInfo.removePlayer(1);
                    Main.zkClient.writeToGameRoomsInfo(gameRoomsInfo);
                    Main.zkClient.releaseWriteLock(lockID);
                    Main.zkClient.decrementPlayerCount();
                } else if (Main.zkClient.pathExists("/game-rooms/2/waiting-players"+client.toString().split(":")[0]+":"+port_num)) {
                    Main.zkClient.removePlayerFromWaitingGameClients(client.toString().split(":")[0] + ":" + port_num, 2);
                    String lockID = Main.zkClient.getWriteLock("/lobby/stats");
                    //go into the room number of the lobby and decrement the number
                    GameRoomsInfo gameRoomsInfo = Main.zkClient.getGameRoomsInfo();
                    gameRoomsInfo.removePlayer(2);
                    Main.zkClient.writeToGameRoomsInfo(gameRoomsInfo);
                    Main.zkClient.releaseWriteLock( lockID);
                    Main.zkClient.decrementPlayerCount();
                } //next would be clauses for the live players, we would kill them in their game and remove them from the list of live players
                //Player action packet
            case 8:
                //send an ACK back
                ByteBuffer buf = ByteBuffer.allocate(1);
                buf.put((byte)9);
                buf.flip();
                channel.send(buf, client);
                //data is what holds the received packet
                //TODO modify game state based on the action
                break;
            //Exit packet
            case 2:
                //send an ack back
                buf = ByteBuffer.allocate(1);
                buf.put((byte)2);
                buf.flip();
                channel.send(buf, client);
                //remove the IP from the list of clients
                break;
            //Enter game room packet
            case 3:
                //if the game is currently not in session & there are less than three people, allow access.
                //otherwise deny entry
                //send an ack with the success or fail
                ByteBuffer ackBuf;
                data.flip();
                EnterRoom enterRoom = new EnterRoom(data);
                String lockId = Main.zkClient.getWriteLock("/lobby/stats");
                GameRoomsInfo gameInfo = Main.zkClient.getGameRoomsInfo();
                if(gameInfo.addPlayer(enterRoom.getRoomNum())){
                    //the room was entered successfully, make a "true" ack packet
                    ackBuf = new Factory().makeEnterRoomAckPacket(true);
                    channel.send(ackBuf, client);
                    //move the player to "waiting game-clients" since they are now in a room.
                    Main.zkClient.addPlayerToWaitingGameClients(client.toString().split(":")[0]+":"+enterRoom.getPortNum(), enterRoom.getRoomNum());
                    //rewrite the lobby stats info with the new gameInfo object
                    Main.zkClient.writeToGameRoomsInfo(gameInfo);
                }else{
                    //room was not successfully entered
                    ackBuf = new Factory().makeEnterRoomAckPacket(false);
                    channel.send(ackBuf, client);
                }
                //release the write lock (very important...)
                Main.zkClient.releaseWriteLock(lockId);
                break;
            default:
                break;
        }

//        // temporary until full packet handling is implemented
//        GameState gs = Main.zkClient.getGameState(0);
//
//        // pretend we got new player packets from the client
//        gs.addPlayer();
//
//        // pretend we got a start packet from the client
//        if (!gs.hasStarted()) { gs.startGame(); }
//
//        // pretend we got an attack packet from the client
//        gs.attack(3);
//
//        // pretend we got a defend packet from the client
//        gs.defend();
//
//        // pretend we got a reload packet from the client
//        gs.reload();
//
//        // if the villain is next
//        if (gs.getCurrentPlayer() == 3) {
//            gs.bossTurn();
//        }

        // the message to be sent back to the client
//        String actionMessage = gs.getActionMessage();
    }

    public void gamePacketHandler(ByteBuffer dataBuf) throws IOException {
        //send an ack back with the data
        switch ((int)dataBuf.get(0)){
            //ack back and make a game action object to work with
            case 8:
                ByteBuffer buf = ByteBuffer.allocate(1);
                buf.put(dataBuf.get(0));
                buf.flip();
                System.out.println("sending to "+client.toString());
                PlayerAction playerAction = new PlayerAction(dataBuf);
                System.out.println(playerAction.getAction());
                System.out.println(playerAction.getPlayerNum());
                System.out.println(playerAction.getGameRoom());
                channel.send(buf, client);
                //modify the game state based on the action. We have a watcher that will broadcast
                //any changes made to the game state

                //first get the write lock for the game state
                String lockVal = Main.zkClient.getWriteLock("/game-rooms/"+playerAction.getGameRoom());
                GameState gs;
                switch (playerAction.getAction()){
                    case 1:
                        //shoot the boss
                        gs = Main.zkClient.getGameState(playerAction.getGameRoom());
                        //index 3 is always the boss
                        gs.attack(3);
                        Main.zkClient.addNewGameState(playerAction.getGameRoom(), gs);
                        break;
                    case 2:
                        //reload guns
                        gs = Main.zkClient.getGameState(playerAction.getGameRoom());
                        //index 3 is always the boss
                        gs.reload();
                        Main.zkClient.addNewGameState(playerAction.getGameRoom(), gs);
                        break;
                    default:
                        break;
                }


                //at the end of everything, release the write lock
                Main.zkClient.releaseWriteLock(lockVal);
                //if the next player is the boss, call their action.
                if(Main.zkClient.getGameState(playerAction.getGameRoom()).getCurrentPlayer() == 3){
                    lockVal = Main.zkClient.getWriteLock("/game-rooms/"+playerAction.getGameRoom());
                    gs = Main.zkClient.getGameState(playerAction.getGameRoom());
                    gs.bossTurn();
                    Main.zkClient.addNewGameState(playerAction.getGameRoom(), gs);
                    Main.zkClient.releaseWriteLock(lockVal);
                }
        }
    }
}