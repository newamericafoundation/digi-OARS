package com.newamerica.webserver.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newamerica.webserver.dtos.Fund;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FundResponse extends ServiceResponse {

    private Fund data;


    public FundResponse() { super();}

    public FundResponse(String message, String resourcePath) {
        super(message, resourcePath);
    }


    public Fund getData() {
        return data;
    }

    public void setData(Fund data) {
        this.data = data;
    }


}
