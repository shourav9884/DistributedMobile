package com.example.nati.rpcjsontest.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by nati on 2/9/18.
 */

public class Job implements Serializable{
    public String jobName;
    public List<Function> functions;
    public int level = 0;
    public Map<Integer, List<Function>> functionMap= new HashMap<>();
    public boolean isDone;
    public String result;
    public String methodName;

    public Job(List<Function> functions, String methodType){
        this.jobName = UUID.randomUUID().toString();
        level = 1;
        for(Function function:functions){
            function.setJobID(jobName);
            function.setMethodType(methodType);
            function.setLevel(level);
        }
        functionMap.put(level, functions);
        this.functions = functions;
        isDone = false;
        this.methodName = methodType;
    }
    public int findFunction(String toFindFunctionId){
        for(int i=0;i<functions.size();i++){
            if (toFindFunctionId.equals(functions.get(i).getFid())) return i;
        }
        return -1;
    }

    public boolean isDoneThisLevel(){
        List<Function> functions = this.functions;
        for(Function function:functions){
            if(function.getLevel()==this.level && !function.isDone()) return false;
        }
        return true;
    }

    public List<Function> getDoneFunctions(){
        if(this.isDoneThisLevel())
            return functionMap.get(level);
        return null;
    }
    public List<Function> getNotDoneFunctions(){
        List<Function> functionList = new ArrayList<>();
        for(Function function:functions){
            if(function.getLevel()==level)
                functionList.add(function);
        }
        return functionList;
    }
    public void setResultInMap(Function function, int level){
        List<Function> functions = this.functionMap.get(level);
        for (Function function1:functions){
            if(function.getFid().equals(function1.getFid())) {
                function1.setResult(function.getResult());
                function.setDone(function.isDone());
            }
        }
    }
    public void generateNewLevelFunction(){
        List<Integer> resultParams = new ArrayList<>();
        for(Function function:this.getDoneFunctions()){
            resultParams.add(function.getResult());
        }
        if(resultParams != null && resultParams.size()>0)
        {
            List<Function> functions = new ArrayList<>();
            int length = resultParams.size();

            while(length>0)
            {
                Function function = new Function();

                function.setResult(null);
                function.setFid(UUID.randomUUID().toString());
                function.setIsSent(false);
                function.setIsReceived(false);
                function.setJobID(this.jobName);
                function.setMethodType(this.methodName);



                List<Integer> paramsTemp = new ArrayList<>();
                int start = functions.size()*2;
                if(length<2)
                {
                    for (int i = start;i<start+length;i++)
                    {
                        paramsTemp.add(resultParams.get(i));
                    }
                }
                else
                {
                    for(int i=start;i<(start + 2);i++)
                    {
                        paramsTemp.add(resultParams.get(i));
                    }
                }
                function.setLevel(this.level+1);
                function.setParams(paramsTemp);
                functions.add(function);
                length -= 2;

            }
            this.functions.addAll(functions);
            this.functionMap.put(this.level+1, functions);
        }
    }
}
