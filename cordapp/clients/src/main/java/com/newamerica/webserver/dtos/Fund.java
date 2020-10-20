package com.newamerica.webserver.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fund {

    @NotNull
    private String originParty;

    @NotNull
    private String receivingParty;

    @NotNull
    private String accountId;

    private String receivedByUsername;

    @NotNull
    public String amount;


    public String getOriginParty() {
        return originParty;
    }

    public void setOriginParty(String originParty) {
        this.originParty = originParty;
    }

    public String getReceivingParty() {
        return receivingParty;
    }

    public void setReceivingParty(String receivingParty) {
        this.receivingParty = receivingParty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceivedByUsername() { return receivedByUsername; }

    public void setReceivedByUsername(String receivedByUsername) { this.receivedByUsername = receivedByUsername; }

    public String getAccountId() { return accountId; }

    public void setAccountId(String accountId) { this.accountId = accountId; }


}
