package com.example.nati.rpcjsontest.utils;

import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nati on 1/26/18.
 */

public class Cache {
    public static HashMap<String, MobileNode> mobileNodeHashMap = new HashMap<>();
    public static HashMap<MobileNode, List<MobileNode>> deadNotification = new HashMap<>();
    public static HashMap<MobileNode, List<FunctionResponse>> toSendNotifications = new HashMap<>();
    public static void addMobileNode(MobileNode mobileNode){
        if(!mobileNodeHashMap.containsKey(mobileNode.name)){
            mobileNodeHashMap.put(mobileNode.name, mobileNode);
        }
    }
    public static void removeMobileNode(String key){
        if(mobileNodeHashMap.containsKey(key)){
            mobileNodeHashMap.remove(key);
        }
    }
    public static void addDeadNode(MobileNode notifier, MobileNode deadNode){
        if(deadNotification.containsKey(notifier)){
            List<MobileNode> deadNodes = deadNotification.get(notifier);
            if(deadNodes == null ) deadNodes = new ArrayList<>();
            deadNodes.add(deadNode);
        }
        else{
            List<MobileNode> deadNodes = new ArrayList<>();
            deadNodes.add(deadNode);
            deadNotification.put(notifier,deadNodes);
        }
    }
    public static List<MobileNode> getNotifiersByDeadNode(MobileNode deadNode){
        Set<MobileNode> resultSet = new HashSet<>();
        for (MobileNode mobileNode:deadNotification.keySet()){
            List<MobileNode> deadNodes = deadNotification.get(mobileNode);
            for(MobileNode d: deadNodes){
                if(d.equals(deadNode)) resultSet.add(mobileNode);
            }
        }
        return new ArrayList<>(resultSet);
    }
    public static MobileNode getNode(String key){
        return mobileNodeHashMap.get(key);
    }
}
