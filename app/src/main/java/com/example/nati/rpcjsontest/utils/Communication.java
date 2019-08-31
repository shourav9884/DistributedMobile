package com.example.nati.rpcjsontest.utils;

import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.Job;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.ResponseEntity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nati on 5/6/16.
 */
public class Communication {

    public static ArrayList<Function> functions = new ArrayList<>();
    public static HashMap<String,Function> fnctionIdMap = new HashMap<>();
    public static HashMap<String, Job> jobHashMap = new HashMap<>();

    public static BlockingQueue<FunctionRequest> requests = new LinkedBlockingQueue<>();
    public static BlockingQueue<FunctionResponse> responses = new LinkedBlockingQueue<>();
    public static BlockingQueue<FunctionResponse> receivedResponses = new LinkedBlockingQueue<>();


    public static  List<MobileNode> availableClients = new ArrayList<>();


    public static synchronized boolean isAllDone()
    {
        for (Function f:functions)
        {
            if (f.getResult() == null && !f.isReceived())
                return false;
        }
        return true;
    }
    public static synchronized Function getUnfinishedFunction()
    {
        for (Function f:functions)
        {
            if (f.getResult() == null && !f.isSent())
                return f;
        }
        return null;
    }

    public static void writeToDOS(String string, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(string);
    }

}
