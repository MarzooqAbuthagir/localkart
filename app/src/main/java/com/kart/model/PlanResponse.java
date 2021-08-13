package com.kart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PlanResponse {

    @SerializedName("errorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("result")
    @Expose
    private Map<String, List<PlanDynamicKeys>> result;

    public Map<String, List<PlanDynamicKeys>> getResult() {
        return result;
    }

    public void setResult(Map<String, List<PlanDynamicKeys>> result) {
        this.result = result;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}