package com.learn.zw.zookeeper.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

public class AbstractZkClient implements  IZKClient{


    @Override
    public CuratorFramework getClient() {
        return null;
    }

    @Override
    public void setData(String path, CreateMode mode, byte[] data) {

    }

    @Override
    public void setData(String path, byte[] data) {

    }

    @Override
    public void setDataParentsIfNeeded(String path, byte[] data) {

    }

    @Override
    public byte[] getData(String path) {
        return new byte[0];
    }

    @Override
    public boolean deleteData(String path) {
        return false;
    }

    @Override
    public boolean isExist(String path) {
        return false;
    }

    @Override
    public void registerPathChildrenCacheListener(PathChildrenCacheListener listener, String path, boolean cacheData) {

    }

    @Override
    public void registerNodeCacheListener(NodeCacheListener listener, String path, boolean dataIsCompressed) {

    }

    @Override
    public void registerTreeCacheListener(TreeCacheListener listener, String path) {

    }

    @Override
    public void registerConnectionStateListener(ConnectionStateListener stateListener) {

    }
}
