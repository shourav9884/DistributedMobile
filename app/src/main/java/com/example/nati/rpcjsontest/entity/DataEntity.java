package com.example.nati.rpcjsontest.entity;

import java.io.Serializable;

/**
 * Created by nati on 4/23/16.
 */
public class DataEntity implements Serializable {
    private String type;
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
