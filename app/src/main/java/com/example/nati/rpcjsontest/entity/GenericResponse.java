package com.example.nati.rpcjsontest.entity;

import java.io.Serializable;

/**
 * Created by nati on 1/26/18.
 */

public class GenericResponse implements Serializable {
    public int status;
    public String msg;

    public GenericResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
