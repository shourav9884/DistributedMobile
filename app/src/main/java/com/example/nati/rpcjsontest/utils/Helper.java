package com.example.nati.rpcjsontest.utils;

import android.util.Log;

import com.example.nati.rpcjsontest.entity.MobileNode;
import com.example.nati.rpcjsontest.entity.RequestEntity;
import com.example.nati.rpcjsontest.entity.ResponseEntity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by nati on 4/17/16.
 */
public class Helper {
    public final static int SERVER_PORT = 9884;
    public static String methodName = "";
    public static String getDeviceIpAddress() {
        String ipAddress = null;
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses(); enumerationIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    System.out.println(inetAddress.getHostAddress());
//                    System.out.println("Fatrami: ");
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        ipAddress = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
            e.printStackTrace();
        }
        return ipAddress;
    }

    public static MobileNode ownMobileNode(String name)
    {
        MobileNode mobileNode = new MobileNode();
        mobileNode.ip = Helper.getDeviceIpAddress();
        mobileNode.port = SERVER_PORT;
        mobileNode.name = name;

        return mobileNode;
    }

}
