package com.newamerica.webserver.responses;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newamerica.states.RequestState;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestResponse extends ServiceResponse {

    private RequestState data;

    public RequestResponse() { super();}

    public RequestResponse(String message, String resourcePath) {
        super(message, resourcePath);
    }

    public RequestState getData() {
        return data;
    }

    public void setData(RequestState data) {
        this.data = data;
    }

}
