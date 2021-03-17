package com.eli.param.cache.core;

import org.apache.zookeeper.CreateMode;

/**
 * @author eli
 */
public interface IZookeeperOperation {


    /**
     * 获取节点数据
     *
     * @param nodePath 节点路径
     * @return new String(byte[])
     */
    String getData(String nodePath);


    /**
     * 是否存在相应节点
     *
     * @param nodePath 节点路径
     * @return true:存在;false:不存在
     */
    boolean isExistPath(String nodePath);


    void createNode(String path, String data, CreateMode createMode);

}
