package org.zk.dataClasses;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
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
    public static ZookeeperData deserialize(byte[] data) throws IOException, ClassNotFoundException {
        byte[] dataOfInterest = Arrays.copyOfRange(data, 2, data.length);
        switch (Byte.toUnsignedInt(data[0])){
            case 1:
                return new ServerData(new String(dataOfInterest));
            case 2:
                return new PublicEncryptionKey(new BigInteger(dataOfInterest));
            case 3:
                ByteArrayInputStream bais = new ByteArrayInputStream(dataOfInterest);
                ObjectInputStream ois = new ObjectInputStream(bais);
                //your IDE will probably throw a warning saying to genrify this but we'll never not put an arraylist of big ints here
                return new ClientKeys((ArrayList<BigInteger>) ois.readObject());
            case 4:
                bais = new ByteArrayInputStream(dataOfInterest);
                ois = new ObjectInputStream(bais);
                //your IDE will probably throw a warning saying to genrify this but we'll never not put a list of GameRoom objects here
                return new GameRoomsInfo((List<GameRoom>) ois.readObject());
            case 5:
                //
                break;
            case 6:
                bais = new ByteArrayInputStream(dataOfInterest);
                ois = new ObjectInputStream(bais);
                return new WaitingClients((ArrayList<String>) ois.readObject());
            //we will never reach the default case unless something horribly wrong has happened
            default: return null;

        }
        //we will never reach this.
        return null;
    }
}
