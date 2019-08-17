package com.learn.zw.zookeeper.factory.impl;

import com.learn.zw.zookeeper.client.ZKClient;
import com.learn.zw.zookeeper.factory.ZKFactory;
import com.learn.zw.zookeeper.starter.config.ZKConfig;

public class ZKFactoryImpl implements ZKFactory {

    @Override
    public ZKClient getZKClient(ZKConfig config) {
        return new ZKClient(config);
    }

}
