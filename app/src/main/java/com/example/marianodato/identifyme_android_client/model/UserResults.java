package com.example.marianodato.identifyme_android_client.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResults {

    @SerializedName("paging")
    @Expose
    private Paging paging;

    @SerializedName("results")
    @Expose
    private List<User> results;

    public UserResults() {
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<User> getResults() {
        return results;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }
}
