package org.zk.watchers;

import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

public class ClientListChangeListener implements IZkChildListener {

    /**
     * When the list of clients changes
     *
     * @param parentPath clientListPath
     * @param list list of child paths
     * @throws Exception
     */
    @Override
    public void handleChildChange(String parentPath, List<String> list) throws Exception {

    }
}
