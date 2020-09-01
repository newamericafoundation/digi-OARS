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
    public String amount;

    @NotNull
    public String maxWithdrawalAmount;


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


    public String getMaxWithdrawalAmount() {
        return maxWithdrawalAmount;
    }

    public void setMaxWithdrawalAmount(String maxWithdrawalAmount) {
        this.maxWithdrawalAmount = maxWithdrawalAmount;
    }
}
