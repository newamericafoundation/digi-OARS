package com.newamerica.webserver;

import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import com.newamerica.webserver.dtos.Fund;
import com.newamerica.webserver.exceptions.CustomizedError;
import com.newamerica.webserver.responses.FundResponse;
import com.newamerica.webserver.responses.RequestResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BaseResource {


    public Response customizeErrorResponse(Response.StatusType type, String message) {
        CustomizedError error = new CustomizedError();
        error.setMessage(message);
        return Response.status(type).entity(error).type(MediaType.APPLICATION_JSON).build();
    }

    public FundResponse createFundSuccessServiceResponse(String message, Object data, String resourcePath) {
        FundResponse response = new FundResponse(message, resourcePath);
        response.setData((FundState) data);
        return response;
    }

    public RequestResponse createRequestSuccessServiceResponse(String message, Object data, String resourcePath) {
        RequestResponse response = new RequestResponse(message, resourcePath);
        response.setData((RequestState) data);
        return response;
    }
}
