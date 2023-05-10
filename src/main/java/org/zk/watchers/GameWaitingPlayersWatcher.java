package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.server.Main;
import org.server.packets.Factory;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class GameWaitingPlayersWatcher implements IZkChildListener {
    @Override
    public void handleChildChange(String gameRoomPath, List<String> playerAddresses) throws Exception {
        for(String add:playerAddresses){
            System.out.println(add);
        }
        //disregard the read-write lock nodes
        playerAddresses.removeIf(address -> address.contains("read-lock") || address.contains("write-lock"));

        //send out the start game packet to the players
        if(playerAddresses.size() == 3){
            //shuffle the players up so they get a random player each time
            Collections.shuffle(playerAddresses);
            //add all the players to the live client list. This list is really used to keep sending heartbeats
            //and to know when to encrypt/decrypt since only live game packets will be encrypted/decrypted
            for(String address:playerAddresses)
                Main.zkClient.addPlayerToLiveGameClients(address, Integer.parseInt(gameRoomPath.split("/")[2]));

            //set up the encryption key
            byte[] symmetricKey = new byte[0];
            switch (gameRoomPath.split("/")[2]) {
                case "0":
                    Main.keyRoom0.genKeySet();
                    symmetricKey = Main.keyRoom0.getKeySetAsJSON();
                    break;
                case "1":
                    Main.keyRoom1.genKeySet();
                    symmetricKey = Main.keyRoom1.getKeySetAsJSON();
                    break;
                case "2":
                    Main.keyRoom2.genKeySet();
                    symmetricKey = Main.keyRoom2.getKeySetAsJSON();
                    break;
            }
            //make initial game state values. and store the json keyset object in zookeeper for the other servers to be able to use


            //and finally, send the game start packet to the players
            for(String address:playerAddresses){
                ByteBuffer gameStartPacket = new Factory().makeGameStartPacket(playerAddresses.indexOf(address), 0, Integer.parseInt(gameRoomPath.split("/")[2]), symmetricKey);
                new Thread(new PacketSender(address, 4, "/game-rooms/0/live-players/"+address, gameStartPacket)).start();
            }
        }
    }
}
