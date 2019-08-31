package com.example.nati.rpcjsontest.entity;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nati on 4/23/16.
 */

public class RequestEntity extends DataEntity{
    private String method;
    private ArrayList<String> params;
    private String id;

    public RequestEntity()
    {
        this.setType("request");
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
