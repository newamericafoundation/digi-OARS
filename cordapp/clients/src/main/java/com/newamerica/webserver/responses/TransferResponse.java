package com.newamerica.webserver.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newamerica.states.TransferState;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponse extends ServiceResponse {
    private TransferState data;

    public TransferResponse() { super();}

    public TransferResponse(String message, String resourcePath) {
        super(message, resourcePath);
    }

    public TransferState getData() {
        return data;
    }

    public void setData(TransferState data) {
        this.data = data;
    }
}
