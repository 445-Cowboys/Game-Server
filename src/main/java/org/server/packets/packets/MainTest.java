package org.server.packets.packets;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;

public class MainTest {

    public static void main(String args[]){

        ByteBuffer hb = new Factory().makeHeartbeatAckPacket();
        HeartbeatAck hba = new HeartbeatAck(hb);

        hba.getOpcode();

        /********************************
         * Test Game Start packet
         *
         *
        try {
            SecretKey symmetricKey = generateSymmetricKey();
            //System.out.println(symmetricKey.getEncoded());

            ByteBuffer gsPack = new Factory().makeGameStartPacket(1, symmetricKey);
            GameStart gs = new GameStart(gsPack);

            System.out.println(gs.getOpcode());
            System.out.println(gs.getCharacter());
            SecretKey symmetricKeyReturn = gs.getSymmetricKey();
            System.out.println(symmetricKeyReturn.equals(symmetricKey));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
         */
        /********************************
         * Test Game Rooms packet
         *

        ByteBuffer trueBuf = new Factory().makeEnterRoomAckPacket(true);
        ByteBuffer falseBuf = new Factory().makeEnterRoomAckPacket(false);

        EnterRoomAck eraTrue = new EnterRoomAck(trueBuf);
        EnterRoomAck eraFalse = new EnterRoomAck(falseBuf);

        System.out.println(eraTrue.getOpcode());
        System.out.println(eraTrue.getResult());
        System.out.println(eraFalse.getResult());
        */

        /********************************
         * Test Game Rooms packet
         *
        ByteBuffer buf = new Factory().makeGameRooms(new int[]{1,1,1},new boolean[]{true,true,false},new int[]{2,2,2},new int[]{3,3,3});
        GameRooms gr = new GameRooms(buf);
        System.out.println(gr.getOpcode());
        for (int i = 0; i < 3; i++) {
            System.out.println(gr.getNumPlayers(i));
            System.out.println(gr.getRoomFull(i));
            System.out.println(gr.getRoomStatus(i));
            System.out.println(gr.getServerStatus(i));
        }*/

        /********************************
         * Test Enter Room packet
         *
        ByteBuffer roomBuf = new Factory().makeEnterRoomPacket(1, "Your usename is ...");
        EnterRoom er = new EnterRoom(roomBuf);
        System.out.println(er.getOpcode());
        System.out.println(er.getRoomNum());
        System.out.println(er.getuserName());
        */

        /********************************
         * Test Player Action packet
         *

        ByteBuffer actionBuf = new Factory().makePlayerActionPacket(1, 2, 3);
        PlayerAction pa = new PlayerAction(actionBuf);
        System.out.println(pa.getOpcode());
        System.out.println(pa.getGameRoom());
        System.out.println(pa.getAction());
        System.out.println(pa.getHeal());

        */

        /********************************
         * Test Game State packet
         *
        int[] playerHealth = {2, 3, 4};
        int[] playerAmmo = {8, 8, 8};
        int[] playerAbilityCD = {0, 1 ,0};

                Factory factory = new Factory();
        //factory.makeGameStatePacket(10, 6, playerHealth, playerAmmo,
        //playerAbilityCD, 1, "This is a test");

        GameState gamestate = new GameState(factory.makeGameStatePacket(10, 6, playerHealth, playerAmmo,
                playerAbilityCD, 1, 1, "This is a test"));

        System.out.println(gamestate.getBossHealth());
        System.out.println(gamestate.getBossAmmo());
        System.out.println(gamestate.getPlayerHealth(1));
        System.out.println(gamestate.getCurrentPlayer());
        System.out.println(gamestate.getBlockNum());
        System.out.println(gamestate.getActionMessage());
         */


    }


    public static SecretKey generateSymmetricKey() throws NoSuchAlgorithmException {
        // Get an instance of the key generator for AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");

        // Generate a 256-bit key
        keyGen.init(256);

        // Generate the symmetric key
        SecretKey symmetricKey = keyGen.generateKey();

        return symmetricKey;
    }
}
