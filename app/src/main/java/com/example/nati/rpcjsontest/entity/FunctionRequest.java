package com.example.nati.rpcjsontest.entity;

/**
 * Created by nati on 2/9/18.
 */

public class FunctionRequest {
    public MobileNode sender;
    public MobileNode receiver;
    public Function function;

    @Override
    public String toString() {
        return "FunctionRequest{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", function=" + function +
                '}';
    }
}
