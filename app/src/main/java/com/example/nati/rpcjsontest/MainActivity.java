package com.example.nati.rpcjsontest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nati.rpcjsontest.entity.Function;
import com.example.nati.rpcjsontest.entity.FunctionRequest;
import com.example.nati.rpcjsontest.entity.Job;
import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;
import com.example.nati.rpcjsontest.server.ClientApi;
import com.example.nati.rpcjsontest.server.DNSApi;
import com.example.nati.rpcjsontest.server.WebServer;
import com.example.nati.rpcjsontest.utils.Communication;
import com.example.nati.rpcjsontest.utils.Helper;
import com.example.nati.rpcjsontest.worker.ReflectionHandler;
import com.example.nati.rpcjsontest.worker.RequestHandler;
import com.example.nati.rpcjsontest.worker.ResponseHandler;
import com.example.nati.rpcjsontest.worker.ResponseSender;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    static Boolean isStarted = false;
    private TextView tvServerInfo;
    private Button button,sendTextButton,generateFunctionBtn, startWebServerBtn, setAsDNSBtn;
    private EditText serverIpEditText,methodNameEditText;
    private ResponseSender responseSender;

    private Thread requestHandler;
    private Thread responseHandler;

    private RequestHandler requestHandlerTask;
    private ResponseHandler responseHandlerTask;
    private WebServer webServer = null;

    private Handler handler;

    private String dnsIP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
        init();
    }
    private void init()
    {
        requestHandlerTask = new RequestHandler();
        responseHandlerTask = new ResponseHandler(this);

        handler = new Handler();

        tvServerInfo = (TextView)findViewById(R.id.tv_server_info);
        tvServerInfo.setText("Ipaddress"+Helper.getDeviceIpAddress()+"\nPort:"+Helper.SERVER_PORT);
        tvServerInfo.setMovementMethod(new ScrollingMovementMethod());

        setMyName();
        logPrint("My name is: "+getName());

        serverIpEditText = (EditText)findViewById(R.id.server_ip_edit_text);
        methodNameEditText = (EditText)findViewById(R.id.emethodNameEditText);


        Log.d("rpc-json","Ipaddress" + Helper.getDeviceIpAddress() + "\nPort:" + Helper.SERVER_PORT);

        button = (Button)findViewById(R.id.button_client_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverIP = serverIpEditText.getText().toString();
                dnsIP = serverIP;
                registerToDNS();
//                clientThread.setServerIP(serverIP);
//                clientThread.start();
                logPrint("connected to "+serverIP+" ");
            }
        });
        generateFunctionBtn = (Button) findViewById(R.id.generate_functions_btn);
        generateFunctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear(generateParams(10));

            }
        });
        sendTextButton = (Button)findViewById(R.id.button);
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                newOperation();

//                clientThread.writeSomeThing(dataToJson(requestEntity));
            }
        });
        startWebServerBtn = (Button)findViewById(R.id.start_webserver_btn);
        setAsDNSBtn = (Button)findViewById(R.id.set_dns_btn);
        setAsDNSBtn.setOnClickListener(view->{
            setAsDNS();
        });



        startWebServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isStarted){
                    startWebServer();
                    startWebServerBtn.setText("Stop Server");
                    logPrint("server started");
                    isStarted = true;

                }
                else{
                    stopServer();
                    startWebServerBtn.setText("Start Server");
                    logPrint("server stopped");
                    isStarted = false;
                }


            }
        });
        responseSender = new ResponseSender(MainActivity.this);
        new Thread(responseSender).start();
//        handler.post(responseSender);

    }

    private void setAsDNS() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_dns", true);
        editor.commit();
    }
    private boolean isDNS(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean isDns = sharedPref.getBoolean("is_dns", false);
        return isDns;
    }
    private void setMyName(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", UUID.randomUUID().toString());
        editor.commit();
    }

    public String getName(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String name = sharedPref.getString("name", "");
        return name;
    }
    private void registerToDNS(){
        MobileNode mobileNode = new MobileNode();
        mobileNode.name = getName();
        mobileNode.ip = Helper.getDeviceIpAddress();
        mobileNode.port = Helper.SERVER_PORT;

        DNSApi dnsApi = new DNSApi(dnsIP,MainActivity.this);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    dnsApi.registerNode(mobileNode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                dnsApi.getAvailableNodes(null);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    private void startWebServer() {
        try {
            if(webServer == null)
                webServer = new WebServer(Helper.getDeviceIpAddress(),Helper.SERVER_PORT, this.getName(), isDNS(), MainActivity.this);
            if(requestHandler == null || !requestHandler.isAlive()){
                requestHandler = new Thread(requestHandlerTask);

            }
            if(responseHandler == null || !responseHandler.isAlive()){
                responseHandler = new Thread(responseHandlerTask);
            }
            requestHandler.start();
            responseHandler.start();
            if(isDNS()){
                MobileNode mobileNode = new MobileNode();
                mobileNode.port = Helper.SERVER_PORT;
                mobileNode.ip = Helper.getDeviceIpAddress();
                mobileNode.name = getName();
                Communication.availableClients.add(mobileNode);
            }
            webServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void stopServer(){
        requestHandler.interrupt();
        responseHandler.interrupt();
        webServer.stop();
    }

    public String getDnsIP(){
        String dnsIP = serverIpEditText.getText().toString();
        return dnsIP;
    }

    public void clear(List<Integer> params)
    {
//        tvServerInfo.setText("");
        Communication.functions = new ArrayList<>();
        Communication.fnctionIdMap = new HashMap<>();
        List<Function> functions = generateFunctions(params,2);

        String methodType = methodNameEditText.getText().toString();

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

            sendRequest(functionList);
//            this.handler.post(runnable);
        }
        logPrint("Generated "+functions.size()+" Functions");
    }
    public void sendRequest(List<Function> functions){
        String dnsIP = serverIpEditText.getText().toString();

        DNSApi dnsApi = new DNSApi(dnsIP, MainActivity.this);

        Runnable runnable = new Runnable() {
            public void run() {
                dnsApi.getAvailableNodes(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        List<MobileNode> availableNodes = Communication.availableClients;
                        if(availableNodes != null && availableNodes.size()>0) {

                            ClientApi clientApi = ClientApi.getInstance(MainActivity.this);
                            int nodeIndex = 0;
                            for (Function function : functions) {
                                FunctionRequest request = new FunctionRequest();
                                request.function = function;
                                request.receiver = availableNodes.get(nodeIndex % availableNodes.size());
                                request.sender = Helper.ownMobileNode(getName());
                                clientApi.sendFunction(request.receiver, request);
                                nodeIndex++;
                            }
                        }
                        return null;
                    }
                });

            }
        };
        new Thread(runnable).start();
    }

    private List<Integer> generateParams(int size)
    {
        List<Integer> params = new ArrayList<>();
        for(int i = 1; i <= size;i++)
        {
            params.add(i);
        }
        return params;
    }
    public List<Function> generateFunctions(List<Integer> params, int pageSize)
    {
        if(params != null && params.size()>0)
        {
            List<Function> functions = new ArrayList<>();
            int length = params.size();

            while(length>0)
            {
                Function function = new Function();

                function.setResult(null);
                function.setFid(UUID.randomUUID().toString());
                function.setIsSent(false);
                function.setIsReceived(false);


                List<Integer> paramsTemp = new ArrayList<>();
                int start = functions.size()*pageSize;
                if(length<pageSize)
                {
                    for (int i = start;i<start+length;i++)
                    {
                        paramsTemp.add(params.get(i));
                    }
                }
                else
                {
                    for(int i=start;i<(start + pageSize);i++)
                    {
                        paramsTemp.add(params.get(i));
                    }
                }
                function.setParams(paramsTemp);
                functions.add(function);
                length -= pageSize;

            }
            return functions;
        }
        return null;
    }

    private String dataToJson(RequestEntity requestEntity)
    {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(requestEntity);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void logPrint(String text)
    {
        final  String temText = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                String old = tvServerInfo.getText().toString();
                old += "\n" + temText+"\n";
                tvServerInfo.setText(old);

            }
        });

    }
}
