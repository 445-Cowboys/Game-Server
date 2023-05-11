package org.zk.watchers;

import org.I0Itec.zkclient.IZkDataListener;
import org.server.Main;
import org.server.packets.Factory;
import org.zk.dataClasses.EncryptionKey;
import org.zk.dataClasses.GameState;
import org.zk.dataClasses.Player;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class GameStateChangeListener implements IZkDataListener {
    @Override
    public void handleDataChange(String path, Object o) throws Exception {
        //cast the object to a GameState type
        GameState gs = ((GameState) o);
        System.out.println("Boy howdy the game state just changed...");
        //if it is zero that means the game is brand new, don't broadcast any changes.
        if(gs.getFrameNum() == 0) return;
        ByteBuffer gameState = new Factory().makeGameStatePacket(gs.getPlayers()[3].getHealth(), gs.getPlayers()[3].getAmmo(), new int[]{gs.getPlayers()[0].getHealth(), gs.getPlayers()[1].getHealth(), gs.getPlayers()[2].getHealth()}, new int[]{gs.getPlayers()[0].getAmmo(), gs.getPlayers()[1].getAmmo(), gs.getPlayers()[2].getAmmo()}, new int[]{gs.getPlayers()[0].getAmmo(), gs.getPlayers()[1].getAmmo(), gs.getPlayers()[2].getAmmo()},gs.getCurrentPlayer(), gs.getFrameNum(), gs.getActionMessage());
        //get the list of clients who are currently in the game, relay the game state change to them
        //encrypt the packet
        EncryptionKey key = Main.zkClient.getEncryptionKey(Integer.parseInt(path.split("/")[2]));
        switch (Integer.parseInt(path.split("/")[2])){
            case 0:
                Main.keyRoom0.parseKey(key.getPublicKey());
                gameState = ByteBuffer.wrap(Main.keyRoom0.encrypt(gameState.array()));
                break;
            case 1:
                Main.keyRoom1.parseKey(key.getPublicKey());
                gameState = ByteBuffer.wrap(Main.keyRoom1.encrypt(gameState.array()));
                break;
            case 2:
                Main.keyRoom2.parseKey(key.getPublicKey());
                gameState = ByteBuffer.wrap(Main.keyRoom2.encrypt(gameState.array()));
                break;
        }

        List<Player> livePlayers = Arrays.asList(Arrays.copyOfRange(gs.getPlayers(), 0, 3));
        livePlayers.removeIf(player ->player.getHealth() <= 0);


        for (String player : Main.zkClient.getGameClients(Integer.parseInt(path.split("/")[2]))) {
                if (player.contains("write-lock") || player.contains("read-lock")) continue;
                //player will hold the ip and listening port in the format "<IP>:<Port>"
                //send the client the new game state.
                System.out.println("sending new game state to " + player);
                new Thread(new PacketSender(player, 9, path + "/live-players/" + player, gameState)).start();
        }

        if(gs.getPlayers()[3].getHealth() <= 0 || livePlayers.size() == 0) {
            //if the boss died or all three players are dead, the game is over and we can go ahead and remove all the players
            //from the list of live-players
            int numOfPlayersStillHere = 0;
            for(String player: Main.zkClient.getGameClients(Integer.parseInt(path.split("/")[2]))){
                if(player.contains("write-lock")||player.contains("read-lock")) continue;
                //player will hold the ip and listening port in the format "<IP>:<Port>"
                //send the client the new game state.
                numOfPlayersStillHere++;
                Main.zkClient.removePlayerFromLiveGameClients(player, Integer.parseInt(path.split("/")[2]));
            }

            //reset the room stats
            Main.zkClient.resetRoom(Integer.parseInt(path.split("/")[2]));
            //subtract the number of players who were still in the game from the live player count
            Main.zkClient.lowerPlayerCount(numOfPlayersStillHere);
        }

    }

    @Override
    public void handleDataDeleted(String path) throws Exception {
        //Don't care about it getting deleted
        //(we never do that, we only change the values)
    }
}
