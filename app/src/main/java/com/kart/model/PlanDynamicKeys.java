package com.kart.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PlanDynamicKeys {

    @SerializedName("benifits")
    @Expose
    private Map<String, String> benifits;

    public Map<String, String> getBenifits() {
        return benifits;
    }

    public void setBenifits(Map<String, String> benifits) {
        this.benifits = benifits;
    }

}