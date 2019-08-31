package com.example.nati.rpcjsontest.worker;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.server.ClientApi;
import com.example.nati.rpcjsontest.utils.Communication;

import java.io.UnsupportedEncodingException;
import java.util.logging.Handler;

/**
 * Created by nati on 2/9/18.
 */

public class ResponseSender implements Runnable {
    private MainActivity activity;

    public ResponseSender(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        FunctionResponse responseEntity;
        try {
            while (true){
                responseEntity = Communication.responses.take();


                ClientApi.getInstance(activity).sendResult(responseEntity.mobileNode, responseEntity);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
