package com.example.nati.rpcjsontest.worker;

import android.os.Handler;
import android.os.Looper;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.Job;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.server.ClientApi;
import com.example.nati.rpcjsontest.server.DNSApi;
import com.example.nati.rpcjsontest.utils.Communication;
import com.example.nati.rpcjsontest.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by nati on 2/10/18.
 */

public class JobCreator implements Runnable {
    MainActivity activity;
    List<Integer> params;
    String methodType;

    public JobCreator(MainActivity activity, List<Integer> params, String methodType){
        this.activity = activity;
        this.params = params;
        this.methodType = methodType;
    }

    @Override
    public void run() {
//        tvServerInfo.setText("");
        Communication.functions = new ArrayList<>();
        Communication.fnctionIdMap = new HashMap<>();
        List<Function> functions = activity.generateFunctions(params,2);

//        String methodType = activity.methodNameEditText.getText().toString();

        Job newJob = new Job(functions, methodType);
        Communication.jobHashMap.put(newJob.jobName, newJob);

        List<Function> functionList = newJob.getNotDoneFunctions();
        if(functionList.size()==1){
            RequestEntity requestEntity = new RequestEntity();

            requestEntity.setParams((ArrayList<String>) functionList.get(0).getStringParams());
            requestEntity.setMethod(functionList.get(0).getMethodType());
            ResponseEntity responseEntity = ReflectionHandler.calculateResult(requestEntity);

            newJob.result = responseEntity.getResult();
            newJob.isDone = true;

            Communication.jobHashMap.put(newJob.jobName, newJob);
        }
        else{
            String dnsIP = activity.getDnsIP();
            DNSApi dnsApi = new DNSApi(dnsIP, activity);

            dnsApi.getAvailableNodes(new Callable() {
                @Override
                public Object call() throws Exception {
                    List<MobileNode> availableNodes = Communication.availableClients;
                    if(availableNodes != null && availableNodes.size()>0) {

                        ClientApi clientApi = ClientApi.getInstance(activity);
                        int nodeIndex = 0;
                        for (Function function : functionList) {
                            FunctionRequest request = new FunctionRequest();
                            request.function = function;
                            request.receiver = availableNodes.get(nodeIndex % availableNodes.size());
                            request.sender = Helper.ownMobileNode(activity.getName());
                            clientApi.sendFunction(request.receiver, request);
                            nodeIndex++;
                        }
                    }
                    return null;
                }
            });

        }


//        Communication.functions = (ArrayList) functions;
//        if(functions !=null) {
//            for (Function f : functions) {
//                Communication.fnctionIdMap.put(f.getFid(), f);
//                System.out.println(f.getFid() + "->" + f.toString());
////                                mainActivity.logPrint();
//
//            }
//        }
        activity.logPrint("Generated "+functions.size()+" Functions");
    }
    public void test(){

    }

}
