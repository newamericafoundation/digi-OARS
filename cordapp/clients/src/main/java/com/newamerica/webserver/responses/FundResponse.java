package com.newamerica.webserver.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newamerica.states.FundState;
import com.newamerica.webserver.dtos.Fund;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FundResponse extends ServiceResponse {

    private FundState data;

    public FundResponse() { super();}

    public FundResponse(String message, String resourcePath) {
        super(message, resourcePath);
    }

    public FundState getData() {
        return data;
    }

    public void setData(FundState data) {
        this.data = data;
    }

}
