package com.learn.zw.zookeeper.controller;

import com.learn.zw.zookeeper.cache.PropertyCache;
import com.learn.zw.zookeeper.cache.PropertyExtCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TestController
 * @Description: TODO
 * @Author: zhang.wei
 * @Date: 2019/3/7 18:23
 * @Version: 1.0
 */
@RestController
public class ZookeeperController {

    //@Autowired
    private PropertyCache propertyCache;

    @Autowired
    private PropertyExtCache propertyExtCache;


    @RequestMapping(value = "/test/get")
    public String test(String path) {
        try {
            //String property = propertyCache.getProperty(path);
            String property = propertyExtCache.getPropertyWithCache(path);
            return property;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/test/delete")
    public String delete(String path) {
        try {
            //propertyCache.deleteProperty(path);
            propertyExtCache.deleteProperty(path);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value = "/test/add")
    public String add(String path, String value) {
        try {
            propertyExtCache.addPropertyParentsIfNeeded(path, value);
            //propertyCache.addPropertyParentsIfNeeded(path, value);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
