package com.learn.zw.zookeeper.client;

import com.learn.zw.zookeeper.constant.ZKConstant;
import com.learn.zw.zookeeper.starter.config.ZKConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @ClassName: ZKClient
 * @Description: TODO
 * @Author: zhang.wei
 * @Date: 2019/3/8 14:52
 * @Version: 1.0
 */
@Slf4j
public class ZKClient extends AbstractZkClient{

    private CuratorFramework client;

    private PathChildrenCache pathChildrenCache;

    private NodeCache nodeCache;

    private TreeCache treeCache;

    public ZKClient(ZKConfig config) {
        // zookeeper连接重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                config.getRetrySleepTime(), config.getRetryCount());
        // 构建CuratorFramework客户端
        client = CuratorFrameworkFactory.builder().connectString(config.getConnectionAddr())
                .connectionTimeoutMs(config.getConnectionTimeout())
                .sessionTimeoutMs(config.getSessionTimeout())
                .retryPolicy(retryPolicy)
                .namespace(ZKConstant.root)
                .build();
        client.start();
    }

    public CuratorFramework getClient() {
        return client;
    }

    public PathChildrenCache getPathChildrenCache() {
        return pathChildrenCache;
    }

    public NodeCache getNodeCache() {
        return nodeCache;
    }

    public TreeCache getTreeCache() {
        return treeCache;
    }

    public void setData(String path, CreateMode mode, byte[] data) {
        try {
            if (!isExist(path)) {
                client.create().withMode(mode).forPath(path, data);
                return;
            }
            client.setData().forPath(path, data);
        } catch (Exception e) {
            log.error("设置属性节点：{}，发生异常：{}", path, e);
        }
    }

    public void setData(String path, byte[] data) {
        try {
            if (!isExist(path)) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(path, data);
                return;
            }
            client.setData().forPath(path, data);
        } catch (Exception e) {
            log.error("更新属性节点：{}，发生异常：{}", path, e);
        }
    }

    public void setDataParentsIfNeeded(String path, byte[] data) {
        try {
            if (!isExist(path)) {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
                return;
            }
            client.setData().forPath(path, data);
        } catch (Exception e) {
            log.error("设置属性节点：{}，发生异常：{}", path, e);
        }
    }



    public byte[] getData(String path) {
        try {
            if (!isExist(path)) {
                return null;
            }
            return client.getData().forPath(path);
        } catch (Exception e) {
            log.error("获取属性节点：{}，发生异常：{}", path, e);
            return null;
        }
    }

    public boolean deleteData(String path) {
        try {
            if (isExist(path)) {
                client.delete().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            log.error("删除属性节点：{}，发生异常：{}", path, e);
            return false;
        }
        return true;
    }

    public boolean isExist(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            return stat == null ? false : true;
        } catch (Exception e) {
            log.error("校验属性节点：{}，是否存在发生异常：{}", path, e);
            return false;
        }
    }

    /**
     *  设置Path Cache, 监控本节点的子节点被创建,更新或者删除，注意是子节点, 子节点下的子节点不能递归监控
     *  事件类型有3个, 可以根据不同的动作触发不同的动作
     *  @Param path 监控的节点路径, cacheData 是否缓存data
     *  可重入监听
     * */
    public void registerPathChildrenCacheListener(PathChildrenCacheListener listener, String path, boolean cacheData) {
        try {
            pathChildrenCache = new PathChildrenCache(client, path, cacheData);
            pathChildrenCache.getListenable().addListener(listener);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            log.error("节点：{}，注册PathChildrenCacheListener监听发生异常：{}", path, e);
        }
    }

    /**
     *  设置Node Cache, 监控本节点的新增,删除,更新
     *  节点的update可以监控到, 如果删除会自动再次创建空节点
     *  @Param path 监控的节点路径, dataIsCompressed 数据是否压缩
     *  不可重入监听
     * */
    public void registerNodeCacheListener(NodeCacheListener listener, String path, boolean dataIsCompressed) {
        try {
            nodeCache = new NodeCache(client, path, dataIsCompressed);
            nodeCache.getListenable().addListener(listener);
            nodeCache.start();
        } catch (Exception e) {
            log.error("节点：{}，注册NodeCacheListener监听发生异常：{}", path, e);
        }
    }

    /**
     *  设置Tree Cache, 监控本节点的新增,删除,更新
     *  节点的update可以监控到, 如果删除不会自动再次创建
     *  @Param path 监控的节点路径, dataIsCompressed 数据是否压缩
     *  可重入监听
     * */
    public void registerTreeCacheListener(TreeCacheListener listener, String path) {
        try {
            treeCache = new TreeCache(client, path);
            treeCache.getListenable().addListener(listener);
            treeCache.start();
        } catch (Exception e) {
            log.error("节点：{}，注册TreeCacheListener监听发生异常：{}", path, e);
        }
    }

    /**
     * 注册zookeeper连接状态监听器
     * @param stateListener
     */
    public void registerConnectionStateListener(ConnectionStateListener stateListener) {
        this.client.getConnectionStateListenable().addListener(stateListener);
    }


}
