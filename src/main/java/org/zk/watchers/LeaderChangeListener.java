package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.Server.Main;

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
            try{
                Main.zkClient.electLeader();
            }catch (ZkNodeExistsException e){
                System.out.println("Master already made");
            }
        }else{
            System.out.println("New leader is "+Main.zkClient.getLeaderNode().getAddress());
        }

    }
}
