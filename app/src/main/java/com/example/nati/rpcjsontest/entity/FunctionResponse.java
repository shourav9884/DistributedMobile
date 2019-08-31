package com.example.nati.rpcjsontest.entity;

/**
 * Created by nati on 2/9/18.
 */

public class FunctionResponse {
    public MobileNode mobileNode;
    public Function function;
    public ResponseEntity response;


    @Override
    public String toString() {
        return "FunctionResponse{" +
                "mobileNode=" + mobileNode +
                ", function=" + function +
                ", response=" + response +
                '}';
    }
}
