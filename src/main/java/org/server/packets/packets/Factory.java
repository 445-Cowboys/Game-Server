package org.server.packets.packets;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;

public class Factory {

    //cast a given int's value to a two byte array
    private byte[] getIntBytes(int x){
        return new byte[]{
                (byte) (x >> 8),
                (byte) x
        };
    }

    public static int bytesToInt(byte[] intBuf) {
        return ByteBuffer.wrap(new byte[]{
                0x00,
                0x00,
                intBuf[1],
                intBuf[0]
        }).getInt();
    }

        public ByteBuffer makeGameStatePacket(int bossHealth, int bossAmmo, int[] playerHealth, int[] playerAmmo,
        int[] playerAbilityCD, int currentPlayer, int blockNumber, String actionMessage) {

            // Build byte buffer stream
            byte[] actionMessageBytes = actionMessage.getBytes();
            int messageLength = actionMessageBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(67 + messageLength); // Total length of packet is 62 bytes

            buffer.put((byte) 0x09);

            buffer.putInt(bossHealth);
            buffer.put((byte) 0);
            buffer.putInt(bossAmmo);
            buffer.put((byte) 0);
            for (int i = 0; i < 3; i++) {
                buffer.putInt(playerHealth[i]);
                buffer.put((byte) 0);
                buffer.putInt(playerAmmo[i]);
                buffer.put((byte) 0);
                buffer.putInt(playerAbilityCD[i]);
                buffer.put((byte) 0);
            }
            buffer.putInt(currentPlayer);
            buffer.put((byte) 0);
            buffer.putInt(blockNumber);
            buffer.put((byte) 0);
            buffer.put(actionMessageBytes);
            buffer.flip();
            return buffer;
        }

        public ByteBuffer makePlayerActionPacket(int gameRoom, int action, int heal){
            ByteBuffer buffer = ByteBuffer.allocate(15); // Total length of packet is 15 bytes

            buffer.put((byte) 0x08);

            buffer.putInt(gameRoom);
            buffer.put((byte) 0);
            buffer.putInt(action);
            buffer.put((byte) 0);
            buffer.putInt(heal);
            buffer.flip();
            return buffer;
        }

        public ByteBuffer makeEnterRoomPacket(int gameRoom, String userName){
            byte[] userNameBytes = userName.getBytes();
            int messageLength = userNameBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(6 + messageLength); // Total length of packet is 6 byte + username

            buffer.put((byte) 0x07);
            buffer.putInt(gameRoom);
            buffer.put((byte) 0);
            buffer.put(userNameBytes);

            buffer.flip();
            return buffer;
        }

        public ByteBuffer makeEnterRoomAckPacket(boolean result){
            ByteBuffer buffer = ByteBuffer.allocate(3);

            buffer.put((byte) 0x06);
            buffer.put((byte) 0);
            buffer.put((byte) (result ? 0x01 : 0x00));

            buffer.flip();
            return buffer;
        }

        public ByteBuffer makeGameRooms(int[] numPlayers, boolean[] roomFull, int[] roomStatus, int[] serverStatus){
            ByteBuffer buffer = ByteBuffer.allocate(43); // Total length of packet is 42 bytes

            buffer.put((byte) 0x05);

            for (int i = 0; i < 3; i++) {
                buffer.putInt(numPlayers[i]);
                buffer.put((byte) (roomFull[i] ? 0x01 : 0x00));
                buffer.putInt(roomStatus[i]);
                buffer.put((byte) 0);
            }
            for (int i = 0; i < 3; i++) {
                buffer.putInt(serverStatus[i]);
            }

            buffer.flip();
            return buffer;
        }


        public ByteBuffer makeGameStartPacket(int character, SecretKey symmetricKey){
            byte[] symmetricKeyBytes = symmetricKey.getEncoded();
            ByteBuffer buffer = ByteBuffer.allocate(7 + symmetricKeyBytes.length);

            buffer.put((byte) 0x04);
            buffer.put((byte) 0);
            buffer.putInt(character);
            buffer.put((byte) 0);
            buffer.put(symmetricKeyBytes);

            buffer.flip();
            return buffer;
        }

        public ByteBuffer makeHeartbeatAckPacket(){
            ByteBuffer buffer = ByteBuffer.allocate(1);

            buffer.put((byte) 0x0c);

            buffer.flip();
            return buffer;
        }



    }
