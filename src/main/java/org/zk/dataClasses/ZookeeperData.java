package org.zk.dataClasses;

import org.checkerframework.checker.units.qual.A;
import org.server.AEAD;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ZookeeperData {
    public ZookeeperData(){}

    public abstract byte[] serialize();

    /**
     * Take in the binary data that was stored in the znode and converts it back into usable data
     * A leading byte of 1 is a leader node, 2 is a public encryption key, 3 is Client encryption key data
     * 4 is Game Rooms Information, and 5 is Game State information.
     * @param data represents the binary data that gets stored in the zookeeper node
     * @return The associated Zookeeper Data object that was stored at the znode
     */
    public static ZookeeperData deserialize(byte[] data) throws IOException, ClassNotFoundException, GeneralSecurityException {
        byte[] dataOfInterest = Arrays.copyOfRange(data, 2, data.length);
        switch (Byte.toUnsignedInt(data[0])){
            case 1:
                return new ServerData(new String(dataOfInterest));
            case 2:
                AEAD aead = new AEAD();
                aead.parseKey(dataOfInterest);
                return new EncryptionKey(aead);
            case 4:
               ByteArrayInputStream bais = new ByteArrayInputStream(dataOfInterest);
                ObjectInputStream ois = new ObjectInputStream(bais);
                //your IDE will probably throw a warning saying to genrify this but we'll never not put a list of GameRoom objects here
                return new GameRoomsInfo((List<GameRoom>) ois.readObject());
            case 5:
                //this one will be for game state
                break;
            case 6:
                return new PlayerCount(dataOfInterest[0]);
            //we will never reach the default case unless something horribly wrong has happened
            default: return null;

        }
        //we will never reach this.
        return null;
    }
}
