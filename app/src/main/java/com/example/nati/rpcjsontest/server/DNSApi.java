package com.example.nati.rpcjsontest.server;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.GenericResponse;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.NodeDeadNotification;
import com.example.nati.rpcjsontest.entity.RegisteredNodeList;
import com.example.nati.rpcjsontest.utils.Communication;
import com.example.nati.rpcjsontest.utils.Helper;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

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

public class DNSApi {
    public String baseUrl;
    private Activity activity;
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public DNSApi(String baseUrl, Activity activity){
        this.baseUrl = baseUrl;
        this.baseUrl = "http://"+baseUrl+":"+ Helper.SERVER_PORT;
        this.activity = activity;
    }

    public void registerNode(MobileNode node) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(new GsonBuilder().create().toJson(node, MobileNode.class));
        asyncHttpClient.post(activity.getApplicationContext(),
                baseUrl+"/nodes/register",
                entity,"application/json",
                new AsyncHttpResponseHandler() {

                    @Override
                    public boolean getUseSynchronousMode() {
                        return false;
                    }

                    @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                System.out.println(string);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void postDeadNotificationToDNS(NodeDeadNotification nodeDeadNotification, Callable callable) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(new GsonBuilder().create().toJson(nodeDeadNotification, NodeDeadNotification.class));
        asyncHttpClient.post(activity.getApplicationContext(), baseUrl + "/nodes/dead", entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String string = new String(responseBody);
                Log.d(TAG, "msg : " + string);
                Log.d(TAG, "status : " + statusCode);

                if (callable != null) {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
    public void getAvailableNodes(Callable callable){
        asyncHttpClient.get(baseUrl + "/nodes", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                RegisteredNodeList registeredNodeList = new GsonBuilder().create().fromJson(response, RegisteredNodeList.class);

                List<MobileNode> mobileNodes = new ArrayList<>();
                for(MobileNode mobileNode:registeredNodeList.mobileNodeList){
                    if (!mobileNode.ip.equals( Helper.getDeviceIpAddress())) mobileNodes.add(mobileNode);
                }

                Communication.availableClients = mobileNodes;

                try {
                    if(callable != null)
                        callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
