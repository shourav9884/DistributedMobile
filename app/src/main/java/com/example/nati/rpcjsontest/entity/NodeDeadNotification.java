package com.example.nati.rpcjsontest.entity;

import java.io.Serializable;

/**
 * Created by nati on 2/3/18.
 */

public class NodeDeadNotification implements Serializable {
    public MobileNode notifier;
    public MobileNode deadNode;
}
