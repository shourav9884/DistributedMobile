package com.example.nati.rpcjsontest.server;

import android.app.Activity;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.FunctionResponse;
import com.example.nati.rpcjsontest.entity.GenericResponse;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.NodeDeadNotification;
import com.example.nati.rpcjsontest.entity.RegisteredNodeList;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.utils.Cache;
import com.example.nati.rpcjsontest.utils.Communication;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by nati on 1/25/18.
 */

public class WebServer extends NanoHTTPD {
    private boolean isDNS = false;
    private MainActivity activity;
    public WebServer(String ip, int port, String name, boolean isDNS, MainActivity activity) {
        super(port);
        this.isDNS = isDNS;
        this.activity = activity;
        if(isDNS){
            MobileNode selfNode = new MobileNode();
            selfNode.ip = ip;
            selfNode.port = port;
            selfNode.name = name;
            Cache.addMobileNode(selfNode);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {


        this.activity.logPrint("Got request: "+session.getUri());

        if(session.getUri().equals("/function-request")){
            if(session.getMethod().equals(Method.POST)){
                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String postBody = files.get("postData");
                    FunctionRequest request = new GsonBuilder().create().fromJson(postBody, FunctionRequest.class);
                    Communication.requests.add(request);
                    return jsonResponse(Response.Status.ACCEPTED, new GenericResponse(Response.Status.OK.ordinal(), "Registration Done"));
                }  catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        }

        else if(session.getUri().equals("/functions-response")){
            if(session.getMethod().equals(Method.POST)){
                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String postBody = files.get("postData");
                    FunctionResponse response = new GsonBuilder().create().fromJson(postBody, FunctionResponse.class);
                    Communication.receivedResponses.add(response);
                    return jsonResponse(Response.Status.ACCEPTED, new GenericResponse(Response.Status.OK.ordinal(), "Registration Done"));
                }  catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(session.getUri().equals("/node-alive")){
            if(session.getMethod().equals(Method.POST)){
                try {
                    Map<String, String> files = new HashMap<String, String>();
                    session.parseBody(files);
                    String postBody = files.get("postData");
                    MobileNode aliveNode = new GsonBuilder().create().fromJson(postBody, MobileNode.class);

                    List<FunctionResponse> responses = Cache.toSendNotifications.get(aliveNode);
                    if(responses !=null) {
                        for (FunctionResponse response : responses) {
                            ClientApi.getInstance(activity).sendResult(aliveNode, response);
                        }
                        Cache.toSendNotifications.remove(aliveNode);
                    }


                    return jsonResponse(Response.Status.ACCEPTED, new GenericResponse(Response.Status.OK.ordinal(), "Alive Node Done"));
                }  catch (IOException e) {
                    e.printStackTrace();
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        }

        if(isDNS){
            try {
                return serveAsDNS(session);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

        }

        return newFixedLengthResponse(Response.Status.OK, "application/json","{\"name\":\"hello\"}");
    }

    private Response serveAsDNS(IHTTPSession session) throws IOException, ResponseException {
        if(session.getUri().equals("/nodes/register")){
            if(session.getMethod().equals(Method.POST)){
                Map<String, String> files = new HashMap<String, String>();
                session.parseBody(files);
                String postBody = files.get("postData");
                MobileNode mobileNode = new GsonBuilder().create().fromJson(postBody, MobileNode.class);
                Cache.addMobileNode(mobileNode);

                activity.logPrint(mobileNode.ip+" connected");
                // get who notified that this was dead
                List<MobileNode> notifiers = Cache.getNotifiersByDeadNode(mobileNode);
                for(MobileNode receiver:notifiers){
                    ClientApi.getInstance(activity).aliveNode(mobileNode, receiver);
                }

                return jsonResponse(Response.Status.ACCEPTED, new GenericResponse(Response.Status.OK.ordinal(), "Registration Done"));
            }
        }
        else if(session.getUri().equals("/nodes")){
            if(session.getMethod().equals(Method.GET)){
                Map<String,MobileNode> map = Cache.mobileNodeHashMap;
                List<MobileNode> mobileNodes = new ArrayList<>();
                for(String key:map.keySet()){
                    mobileNodes.add(map.get(key));
                }
                return jsonResponse(Response.Status.OK, new RegisteredNodeList(mobileNodes));

            }
        }
        // handle notification that mobile is dead
        else if(session.getUri().equals("/nodes/dead")){
            if(session.getMethod().equals(Method.POST)){
                Map<String, String> files = new HashMap<String, String>();
                session.parseBody(files);
                String postBody = files.get("postData");
                NodeDeadNotification deadNotification = new GsonBuilder().create().fromJson(postBody, NodeDeadNotification.class);

                Cache.addDeadNode(deadNotification.notifier, deadNotification.deadNode);
                Cache.removeMobileNode(deadNotification.deadNode.name);

                return jsonResponse(Response.Status.ACCEPTED, new GenericResponse(Response.Status.OK.ordinal(), "Registration Done"));
            }
        }

        return jsonResponse(Response.Status.BAD_REQUEST, new GenericResponse(Response.Status.BAD_REQUEST.ordinal(), "No such Method"));
    }
    private Response jsonResponse(Response.Status status, GenericResponse response){
        return jsonResponse(status, new GsonBuilder().create().toJson(response));
    }
    private Response jsonResponse(Response.Status status, String body){
        return newFixedLengthResponse(status, "application/json", body);
    }
}
