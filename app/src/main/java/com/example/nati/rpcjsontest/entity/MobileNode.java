package com.example.nati.rpcjsontest.entity;

import java.io.Serializable;

/**
 * Created by nati on 1/26/18.
 */

public class MobileNode implements Serializable {
    public String name;
    public String ip;
    public int port;

    @Override
    public String toString() {
        return "MobileNode{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
