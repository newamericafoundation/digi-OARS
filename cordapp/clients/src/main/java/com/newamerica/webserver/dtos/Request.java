package com.newamerica.webserver.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    @NotNull
    private String authorizedUserUsername;

    @NotNull
    private String authorizedUserDept;

    private String authorizerUserUsername;

    @NotNull
    private String externalAccountId;

    @NotNull
    private String purpose;

    @NotNull
    private String amount;

    private String fundStateLinearId;

    public String getPurpose() { return purpose; }

    public void setPurpose(String purpose) { this.purpose = purpose; }


    public String getAuthorizedUserUsername() {
        return authorizedUserUsername;
    }

    public void setAuthorizedUserUsername(String authorizedUserUsername) {
        this.authorizedUserUsername = authorizedUserUsername;
    }

    public String getAuthorizedUserDept() {
        return authorizedUserDept;
    }

    public void setAuthorizedUserDept(String authorizedUserDept) {
        this.authorizedUserDept = authorizedUserDept;
    }

    public String getAuthorizerUserUsername() {
        return authorizerUserUsername;
    }

    public void setAuthorizerUserUsername(String authorizerUserUsername) {
        this.authorizerUserUsername = authorizerUserUsername;
    }

    public String getExternalAccountId() {
        return externalAccountId;
    }

    public void setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFundStateLinearId() {
        return fundStateLinearId;
    }

    public void setFundStateLinearId(String fundStateLinearId) {
        this.fundStateLinearId = fundStateLinearId;
    }
}
