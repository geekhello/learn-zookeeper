package com.learn.zw.zookeeper.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;

public interface IZKClient {

    public CuratorFramework getClient();

    void setData(String path, CreateMode mode, byte[] data);

    void setData(String path, byte[] data);

    void setDataParentsIfNeeded(String path, byte[] data);

    byte[] getData(String path);

    boolean deleteData(String path);

    boolean isExist(String path);

    void registerPathChildrenCacheListener(PathChildrenCacheListener listener, String path, boolean cacheData);

    void registerNodeCacheListener(NodeCacheListener listener, String path, boolean dataIsCompressed);

    void registerTreeCacheListener(TreeCacheListener listener, String path);

    void registerConnectionStateListener(ConnectionStateListener stateListener);
}
