package com.example.nati.rpcjsontest.worker;

import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.utils.Communication;

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

/**
 * Created by nati on 2/9/18.
 */

public class RequestHandler implements Runnable {



    @Override
    public void run() {
        FunctionRequest function;
        try {
            while (true) {
                function = Communication.requests.take();
                System.out.println(function);
                RequestEntity request = new RequestEntity();
                request.setMethod(function.function.getMethodType());
                request.setParams((ArrayList<String>) function.function.getStringParams());
                ResponseEntity responseEntity = ReflectionHandler.calculateResult(request);

                FunctionResponse response = new FunctionResponse();
                response.function = function.function;
                response.mobileNode = function.sender;
                response.function.setResult(Integer.parseInt(responseEntity.getResult()));
                response.response = responseEntity;
                response.function.setDone(true);
                Communication.responses.add(response);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
