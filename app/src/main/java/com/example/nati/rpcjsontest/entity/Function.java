package com.example.nati.rpcjsontest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nati on 5/7/16.
 */
public class Function {
    private List<Integer> params;
    private String fid;
    private String jobID;
    private Integer level;
    private Integer result;
    private boolean isSent;
    private boolean isReceived;
    private boolean isDone;
    private String methodType;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public List<Integer> getParams() {
        return params;
    }

    public List<String> getStringParams(){
        List<String> stringList = new ArrayList<>();
        for (Integer param:params){
            stringList.add(param.toString());
        }
        return stringList;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Function{" +
                "params=" + params +
                ", fid='" + fid + '\'' +
                ", jobID='" + jobID + '\'' +
                ", level=" + level +
                ", result=" + result +
                ", isSent=" + isSent +
                ", isReceived=" + isReceived +
                ", isDone=" + isDone +
                ", methodType='" + methodType + '\'' +
                '}';
    }
}
