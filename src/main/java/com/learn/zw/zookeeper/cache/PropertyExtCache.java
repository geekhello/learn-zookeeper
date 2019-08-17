package com.learn.zw.zookeeper.cache;

import com.learn.zw.zookeeper.client.ZKExtClient;
import com.learn.zw.zookeeper.constant.ZKConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * @ClassName: PropertyExtCacheClient
 * @Description: TODO
 * @Author: zhang.wei
 * @Date: 2019/3/7 16:22
 * @Version: 1.0
 */
@Slf4j
public class PropertyExtCache {

    private ZKExtClient zkClient;
    /**
     * 环境变量
     */
    private String profile;
    /**
     * 属性栏目
     */
    private String[] modules;

    public PropertyExtCache(ZKExtClient client, String profile, String[] modules) {
        log.info("属性预加载环境：{}", profile);
        this.profile = profile;
        this.zkClient = client;
        this.modules = modules;
        if (modules != null && modules.length > 0) {
            for (int i = 0; i < modules.length; i++) {
                String module = modules[i];
                log.info("已订阅栏目路径：{}", module);
                this.zkClient.registerPathChildrenCacheListener(new CacheListener1(ZKConstant.separator + profile + module),
                        ZKConstant.separator + profile + module, true);
            }
        } else {
            log.warn("未发现待订阅栏目路径");
        }
    }

    /**
     * 根据属性节点路径从缓存中获取属性值
     *
     * @param path
     * @return
     * @throws Exception
     */
    public String getPropertyWithCache(String path) throws Exception {
        String key = path.substring(0, path.lastIndexOf("/"));
        PathChildrenCache pathChildrenCache = zkClient.getPathChildrenCache(getPrefixPath() + key);
        if (pathChildrenCache == null) {
            log.warn("预加载环境未发现路径为：{}的属性节点", getPrefixPath() + key);
            return null;
        }
        final ChildData currentData = pathChildrenCache.getCurrentData(getPrefixPath() + path);
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
     * 自定义节点状态监听器
     */
    class CacheListener1 implements PathChildrenCacheListener {

        private String path;

        public CacheListener1(String path) {
            this.path = path;
        }

        @Override
        public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
            switch (event.getType()) {
                case CHILD_ADDED:
                    log.info("监听路径：{}，新增属性节点CHILD_ADDED，属性路径：{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case CHILD_UPDATED:
                    log.info("监听路径：{}，更新属性节点CHILD_UPDATED，属性路径；{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case CHILD_REMOVED:
                    log.info("监听路径：{}，移除属性节点NODE_REMOVED，属性路径；{}，属性值：{}", path,
                            event.getData().getPath(), new String(event.getData().getData(), "utf-8"));
                    break;
                case CONNECTION_LOST:
                    log.warn("监听路径：{}，连接丢失：CONNECTION_LOST", path);
                    break;
                case CONNECTION_SUSPENDED:
                    log.warn("监听路径：{}，连接挂起：CONNECTION_SUSPENDED", path);
                    break;
                case CONNECTION_RECONNECTED:
                    log.warn("监听路径：{}，重新连接：CONNECTION_RECONNECTED", path);
                    break;
                case INITIALIZED:
                    log.warn("监听路径：{}，连接初始化：INITIALIZED", path);
                    break;
                default:
                    log.warn("监听到未定义状态变化类型");
                    break;
            }
        }
    }

}
