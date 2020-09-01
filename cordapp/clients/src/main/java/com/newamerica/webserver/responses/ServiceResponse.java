package com.newamerica.webserver.responses;

import java.io.Serializable;

public class ServiceResponse implements Serializable {
    private String message;

    private String resourcePath;
    /**
     * Default constructor
     */
    public ServiceResponse() {
        super();
    }

    /**
     * Create <code>ServiceResponse</code> object with given fields
     * @param message the response message
     * @param resourcePath the response path
     */
    public ServiceResponse(String message, String resourcePath) {
        super();
        this.message = message;
        this.resourcePath = resourcePath;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the resourcePath
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * @param resourcePath the resourcePath to set
     */
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }



}
