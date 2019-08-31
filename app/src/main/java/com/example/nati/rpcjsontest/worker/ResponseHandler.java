package com.example.nati.rpcjsontest.worker;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.Job;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.server.ClientApi;
import com.example.nati.rpcjsontest.server.DNSApi;
import com.example.nati.rpcjsontest.utils.Cache;
import com.example.nati.rpcjsontest.utils.Communication;
import com.example.nati.rpcjsontest.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by nati on 2/9/18.
 */

public class ResponseHandler implements Runnable {

    MainActivity mainActivity;
    public ResponseHandler(MainActivity activity){
        this.mainActivity = activity;

    }

    @Override
    public void run() {
        FunctionResponse response;
        try {
            while (true){
                response = Communication.receivedResponses.take();
                System.out.println(response.toString());
                Job job = Communication.jobHashMap.get(response.function.getJobID());
                int index = -1;
                if(job!=null) index = job.findFunction(response.function.getFid());
                if(index != -1){
                    if(response.response.getError()==null){
                        job.functions.set(index, response.function);
                        job.setResultInMap(response.function, job.level);
                        mainActivity.logPrint("Response:"+response.function.getMethodType()+",\r\n params: "+response.function.getStringParams()+"\r\n = "+response.function.getResult()+"\r\n");
                        if(job.isDoneThisLevel()){
                            job.generateNewLevelFunction();
                            job.level++;
                            List<Function> functionList = job.getNotDoneFunctions();
                            if(functionList.size()==1){
                                RequestEntity requestEntity = new RequestEntity();

                                requestEntity.setParams((ArrayList<String>) job.getNotDoneFunctions().get(0).getStringParams());
                                requestEntity.setMethod(job.getNotDoneFunctions().get(0).getMethodType());
                                ResponseEntity responseEntity = ReflectionHandler.calculateResult(requestEntity);

                                job.result = responseEntity.getResult();
                                job.isDone = true;

                                mainActivity.logPrint("======Final Result=====");
                                mainActivity.logPrint(responseEntity.getResult()+"");
                                Communication.jobHashMap.put(job.jobName, job);
                            } else {
                                String dnsIP = mainActivity.getDnsIP();
                                DNSApi dnsApi = new DNSApi(dnsIP, mainActivity);

                                dnsApi.getAvailableNodes(new Callable() {
                                    @Override
                                    public Object call() throws Exception {
                                        List<MobileNode> availableNodes = Communication.availableClients;


                                        ClientApi clientApi = ClientApi.getInstance(mainActivity);
                                        int nodeIndex = 0;
                                        for (Function function:functionList){
                                            FunctionRequest request = new FunctionRequest();
                                            request.function = function;
                                            request.receiver = availableNodes.get(nodeIndex%availableNodes.size());
                                            request.sender = Helper.ownMobileNode(mainActivity.getName());
                                            clientApi.sendFunction(request.receiver, request);
                                            nodeIndex++;
                                        }
                                        return null;
                                    }
                                });

                            }
                        }

                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
