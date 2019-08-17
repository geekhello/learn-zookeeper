package com.learn.zw.zookeeper.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("zkConfig")
@ConfigurationProperties(value = "learn.zk")
@Data
public class ZKConfig {

    /**
     * zookeeper连接地址
     */
    private String connectionAddr;
    /**
     * zookeeper重连等待时间（毫秒）
     */
    private int retrySleepTime;
    /**
     * zookeeper重连次数
     */
    private int retryCount;
    /**
     * zookeeper会话超时时长（毫秒）
     */
    private int sessionTimeout;
    /**
     * zookeeper连接超时时长（毫秒
     */
    private int connectionTimeout;

}
