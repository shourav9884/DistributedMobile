package com.example.nati.rpcjsontest.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by nati on 1/26/18.
 */

public class RegisteredNodeList extends GenericResponse {
    public List<MobileNode> mobileNodeList;
    public RegisteredNodeList(List<MobileNode> mobileNodeList) {
        super(NanoHTTPD.Response.Status.OK.ordinal(), "Success");
        this.mobileNodeList = mobileNodeList;
    }

    public RegisteredNodeList() {
        super(NanoHTTPD.Response.Status.OK.ordinal(), "Success");
        this.mobileNodeList = new ArrayList<>();
    }
}
