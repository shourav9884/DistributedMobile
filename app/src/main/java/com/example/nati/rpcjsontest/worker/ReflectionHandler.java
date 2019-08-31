package com.example.nati.rpcjsontest.worker;

import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.utils.Communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Created by nati on 4/29/16.
 */
public class ReflectionHandler {
    public static ResponseEntity calculateResult(RequestEntity requestEntity){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setId(requestEntity.getId());
        Class object = MainWorker.class;
        try {
            Method method = object.getMethod(requestEntity.getMethod(),new Class[]{Integer.class,Integer.class});
//            method.getP
            if(requestEntity.getParams() != null && requestEntity.getParams().size()==method.getGenericParameterTypes().length)
            {
                int result = (Integer) method.invoke(null,Integer.parseInt(requestEntity.getParams().get(0)),Integer.parseInt(requestEntity.getParams().get(1)));
                System.out.println(result);
                responseEntity.setResult(result + "");
            }
            else if(requestEntity.getParams() != null && requestEntity.getParams().size()==1)
            {
                int result = 0;
                try {
                    result = Integer.parseInt(requestEntity.getParams().get(0));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                responseEntity.setResult(result+"");

            }
            else
            {
                responseEntity.setError("Invalid Params");
            }
            Thread.sleep(2500);

        } catch (NoSuchMethodException e) {
            responseEntity.setError("Method not Found");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            responseEntity.setError("InvocationTargetException");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            responseEntity.setError("IllegalAccessException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return responseEntity;
    }
    public static void getResult(RequestEntity requestEntity, CompletableFuture<ResponseEntity> future)
    {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setId(requestEntity.getId());
        Class object = MainWorker.class;
        try {
            Method method = object.getMethod(requestEntity.getMethod(),new Class[]{Integer.class,Integer.class});
//            method.getP
            if(requestEntity.getParams() != null && requestEntity.getParams().size()==method.getGenericParameterTypes().length)
            {
                int result = (Integer) method.invoke(null,Integer.parseInt(requestEntity.getParams().get(0)),Integer.parseInt(requestEntity.getParams().get(1)));
                System.out.println(result);
                responseEntity.setResult(result + "");
            }
            else if(requestEntity.getParams() != null && requestEntity.getParams().size()==1)
            {
                int result = 0;
                try {
                    result = Integer.parseInt(requestEntity.getParams().get(0));
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                responseEntity.setResult(result+"");

            }
            else
            {
                responseEntity.setError("Invalid Params");
            }
            Thread.sleep(2500);

        } catch (NoSuchMethodException e) {
            responseEntity.setError("Method not Found");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            responseEntity.setError("InvocationTargetException");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            responseEntity.setError("IllegalAccessException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        future.complete(responseEntity);
    }

    public static void handleRequestAsync(RequestEntity requestEntity, DataOutputStream outputStream) throws ExecutionException, InterruptedException {
//        java.util.concurrent.CompletableFuture
        CompletableFuture<ResponseEntity> completableFuture = new CompletableFuture<>();
        completableFuture.supplyAsync(()->{
            getResult(requestEntity, completableFuture);
            return null;
        });
        completableFuture.thenAccept(responseEntity -> {
            try {
                Communication.writeToDOS(responseEntity.toJson(), outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
