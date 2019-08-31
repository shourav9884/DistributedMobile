package com.example.nati.rpcjsontest.client;

import android.util.Log;

import com.example.nati.rpcjsontest.MainActivity;
import com.example.nati.rpcjsontest.entity.DataEntity;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.worker.ReflectionHandler;
import com.google.gson.GsonBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

/**
 * Created by nati on 4/17/16
 * .
 */
public class ClientThread extends Thread{
    private String serverIP;
    private int serverPort;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private static ClientThread clientThread = null;

    private MainActivity activity;

    public ClientThread(String serverIP,int port, MainActivity activity)
    {
        this.serverIP = serverIP;
        this.serverPort = port;
        this.activity = activity;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIP,serverPort);
//            socket.
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (true)
            {
                String text = dataInputStream.readUTF();
                Log.d("client",text);
//                this.activity.logPrint(text);
                DataEntity data=new GsonBuilder().create().fromJson(text,DataEntity.class);
                System.out.println(text);
                System.out.println(data.getType());
//                System.out.println(clientSocket.getInetAddress().getHostAddress());
                if(data.getType().equals("request"))
                {
                    RequestEntity requestEntity = new GsonBuilder().create().fromJson(text,RequestEntity.class);
                    ReflectionHandler.handleRequestAsync(requestEntity,dataOutputStream);

                }
//                activity.logPrint("Message from client: #" + this.count);
                activity.logPrint(text);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
    public void writeSomeThing(String text)
    {
        if(dataOutputStream != null)
        {
            try {
                dataOutputStream.writeUTF(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
