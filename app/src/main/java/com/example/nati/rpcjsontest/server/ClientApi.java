package com.example.nati.rpcjsontest.server;

import android.util.Log;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.GenericResponse;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.NodeDeadNotification;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.utils.Cache;
import com.example.nati.rpcjsontest.utils.Helper;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static android.content.ContentValues.TAG;

/**
 * Created by nati on 2/9/18.
 */

public class ClientApi {
    private static ClientApi clientApi;
    private static MainActivity activity;
    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public static ClientApi getInstance(MainActivity activity){
        if(clientApi==null){
            clientApi = new ClientApi();
        }
        ClientApi.activity = activity;
        return clientApi;
    }

    public void sendFunction(MobileNode mobileNode, FunctionRequest function) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(new GsonBuilder().create().toJson(function, FunctionRequest.class));
        asyncHttpClient.post(activity.getApplicationContext(), "http://" + mobileNode.ip + ":" + mobileNode.port + "/function-request", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                System.out.println(new String(responseBody));

                NodeDeadNotification deadNotification = new NodeDeadNotification();
                deadNotification.deadNode = mobileNode;
                deadNotification.notifier = Helper.ownMobileNode(activity.getName());

                DNSApi dnsApi = new DNSApi(activity.getDnsIP(), activity);
                try {
                    dnsApi.postDeadNotificationToDNS(deadNotification, new Callable() {
                        @Override
                        public Object call() throws Exception {
                            List<Function> functions = new ArrayList<>();
                            functions.add(function.function);
                            activity.sendRequest(functions);
                            return null;
                        }
                    });

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }
        });
    }

    public void sendResult(MobileNode mobileNode, FunctionResponse response) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(response, FunctionResponse.class));
        asyncHttpClient.post(activity.getApplicationContext(), "http://" + mobileNode.ip + ":" + mobileNode.port + "/functions-response", stringEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(new String(responseBody));
                if (!Cache.toSendNotifications.containsKey(mobileNode)) {
                    Cache.toSendNotifications.put(mobileNode, new ArrayList<>());
                }
                Cache.toSendNotifications.get(mobileNode).add(response);

                NodeDeadNotification deadNotification = new NodeDeadNotification();
                deadNotification.deadNode = mobileNode;
                deadNotification.notifier = Helper.ownMobileNode(activity.getName());

                DNSApi dnsApi = new DNSApi(activity.getDnsIP(), activity);
                try {
                    dnsApi.postDeadNotificationToDNS(deadNotification, null);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }
        });
    }

    public void aliveNode(MobileNode mobileNode, MobileNode receiverNode) throws UnsupportedEncodingException {
        StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(mobileNode, MobileNode.class));
        asyncHttpClient.post(activity.getApplicationContext(), "http://" + receiverNode.ip + ":" + receiverNode.port + "/node-alive", stringEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            @Override
            public boolean getUseSynchronousMode() {
                return false;
            }
        });
    }
}
