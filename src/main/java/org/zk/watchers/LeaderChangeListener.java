package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.server.Main;

import java.util.List;

public class LeaderChangeListener implements IZkChildListener {

    /**
     *
     * @param parentPath the location of the parent directory
     * @param children the leader node, will be empty if the leader is gone
     * @throws Exception
     */
    @Override
    public void handleChildChange(String parentPath, List<String> children) throws Exception {
        if(children.isEmpty()){
            System.out.println("Leader disconnected, reelecting leader");
            System.out.println(Thread.currentThread().getId());
            try{
                //set it so that there is no delay in sending heartbeat acks, we only want an initial delay
                //if this is the first time the servers start up.
                Main.zkClient.electLeader(0);
            }catch (ZkNodeExistsException e){
                System.out.println("Master already made");
            }
        }else{
            System.out.println("New leader is "+Main.zkClient.getLeaderNode().getAddress());
        }

    }
}
