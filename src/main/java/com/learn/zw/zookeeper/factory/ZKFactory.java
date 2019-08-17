package com.learn.zw.zookeeper.factory;


import com.learn.zw.zookeeper.client.ZKClient;
import com.learn.zw.zookeeper.starter.config.ZKConfig;

public interface ZKFactory {

    ZKClient getZKClient(ZKConfig config);
}
