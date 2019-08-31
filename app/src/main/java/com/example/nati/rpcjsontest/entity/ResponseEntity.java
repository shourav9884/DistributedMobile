package com.example.nati.rpcjsontest.entity;

import com.google.gson.GsonBuilder;

import java.io.Serializable;

/**
 * Created by nati on 4/23/16.
 */
public class ResponseEntity  extends DataEntity{
    private String result;
    private String error;
    private String id;

    public ResponseEntity()
    {
        this.setType("response");
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String toJson(){
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "result='" + result + '\'' +
                ", error='" + error + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
