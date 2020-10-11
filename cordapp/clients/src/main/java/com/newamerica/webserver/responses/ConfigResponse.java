package com.newamerica.webserver.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.newamerica.states.ConfigState;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigResponse extends ServiceResponse {

    private ConfigState data;

    public ConfigResponse() { super();}

    public ConfigResponse(String message, String resourcePath) {
        super(message, resourcePath);
    }

    public ConfigState getData() {
        return data;
    }

    public void setData(ConfigState data) {
        this.data = data;
    }

}