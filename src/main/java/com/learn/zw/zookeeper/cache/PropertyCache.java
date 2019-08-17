package com.learn.zw.zookeeper.cache;

import com.learn.zw.zookeeper.client.ZKClient;
import com.learn.zw.zookeeper.constant.ZKConstant;
import com.learn.zw.zookeeper.starter.config.ZKConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * @ClassName: PropertyCacheClient
 * @Description: TODO
 * @Author: zhang.wei
 * @Date: 2019/3/7 16:22
 * @Version: 1.0
 */
@Slf4j
public class PropertyCache {

    // 节点操作客户端
    private ZKClient zkClient;

    // 环境：dev、prd
    private String profile;

    public PropertyCache(ZKConfig config, String profile) {
        this.profile = profile;
        this.zkClient = new ZKClient(config);
        this.zkClient.registerTreeCacheListener(new CacheListener2(profile), ZKConstant.separator + profile);
    }

    /**
     * 根据属性节点路径从缓存中获取属性值
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String getProperty(String path) throws Exception {
        ChildData currentData = zkClient.getTreeCache().getCurrentData(getPrefixPath() + path);
        if (currentData == null) {
            log.warn("未发现路径为：{}的属性节点", path);
            return null;
        }
        byte[] bytes = currentData.getData();
        String data = new String(bytes, "utf-8");
        log.info("获取当前属性路径：" + path + "，属性值：" + (data.length() > 15 ? data.substring(0, 15) : data));
        return data;
    }

    /**
     * 删除属性节点，级联删除字节点
     *
     * @param path
     */
    public void deleteProperty(String path) {
        zkClient.deleteData(getPrefixPath() + path);
    }

    /**
     * 添加属性节点，父路径必须已存在
     *
     * @param path
     * @param value
     */
    public void addProperty(String path, String value) {
        zkClient.setData(getPrefixPath() + path, value.getBytes());
    }

    /**
     * 添加属性节点，父路径可不存在
     *
     * @param path
     * @param value
     */
    public void addPropertyParentsIfNeeded(String path, String value) {
        zkClient.setDataParentsIfNeeded(getPrefixPath() + path, value.getBytes());
    }

    /**
     * 获取当前环境下根节点路径，如：/dev,/prd
     *
     * @return
     */
    public String getPrefixPath() {
        return ZKConstant.separator + profile;
    }

    /**
     * 自定义TreeCache类型监听器，用来监听当前路径下的所有节点的状态
     */
    class CacheListener2 implements TreeCacheListener {

        private String path;

        public CacheListener2(String path) {
            this.path = path;
        }

        @Override
        public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
            switch (event.getType()) {
                case NODE_ADDED:
                    log.info("监听路径：{}，新增属性节点CHILD_ADDED，属性路径：{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case NODE_UPDATED:
                    log.info("监听路径：{}，更新属性节点CHILD_UPDATED，属性路径；{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case NODE_REMOVED:
                    log.info("监听路径：{}，移除属性节点NODE_REMOVED，属性路径；{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case CONNECTION_LOST:
                    log.warn("监听路径：{}，连接丢失：CONNECTION_LOST", path);
                    break;
                case CONNECTION_RECONNECTED:
                    log.warn("监听路径：{}，重新连接：CONNECTION_RECONNECTED", path);
                    break;
                case INITIALIZED:
                    log.warn("监听路径：{}，连接初始化：INITIALIZED", path);
                    break;
                case CONNECTION_SUSPENDED:
                    log.warn("监听路径：{}，连接挂起：CONNECTION_SUSPENDED", path);
                    break;
                default:
                    log.warn("监听到未定义状态变化类型");
                    break;
            }
        }
    }

}
