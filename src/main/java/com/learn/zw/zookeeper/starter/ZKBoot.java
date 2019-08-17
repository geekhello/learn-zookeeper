package com.learn.zw.zookeeper.starter;

import com.learn.zw.zookeeper.cache.PropertyCache;
import com.learn.zw.zookeeper.cache.PropertyExtCache;
import com.learn.zw.zookeeper.client.ZKExtClient;
import com.learn.zw.zookeeper.starter.config.ZKConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZKBoot {

    @Value("${learn.zk.profile}")
    private String profile;

    @Value("${learn.zk.modules}")
    private String[] modules;

    /**
     * 此bean用来操作PathChildrenCache节点，可加载多个不同的路径下的一级子节点
     * 注：PathChildrenCache特性
     * @param config
     * @return
     */
    @Bean
    public ZKExtClient zkExtClient(@Qualifier("zkConfig") ZKConfig config) {
        return  new ZKExtClient(config);
    }

    @Bean
    public PropertyExtCache propertyExtCache(ZKExtClient zkExtClient) {
        return new PropertyExtCache(zkExtClient, profile, modules);
    }

    /**
     * 此bean用来操作TreeCache类型节点，加载指定路径下所有节点
     * 注：TreeCahce特性
     * @param config
     * @return
     */
    //@Bean
    public PropertyCache propertyCache(@Qualifier("zkConfig") ZKConfig config) {
        return new PropertyCache(config, profile);
    }

}
