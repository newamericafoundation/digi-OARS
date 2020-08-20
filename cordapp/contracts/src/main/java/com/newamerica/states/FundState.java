package com.newamerica.states;

import com.template.contracts.TemplateContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(TemplateContract.class)
public class FundState implements ContractState {
    public final Party originCountry;
    public final Party targetCountry;
    public final double amount;
    public final double balance;
    public final Date datetime;
    public final double maxWithdrawalAmount;
    public final Currency currency;
    public final String status;
    public FundState(Party originCountry, Party targetCountry, double amount, double balance, Date datetime, double maxWithdrawalAmount, Currency currency, String status) {
        this.originCountry = originCountry;
        this.targetCountry = targetCountry;
        this.amount = amount;
        this.balance = balance;
        this.datetime = datetime;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.currency = currency;
        this.status = status;
    }
    //getters
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList();
    }
    public Party getOriginCountry() {        return originCountry;    }
    public Party getTargetCountry() {        return targetCountry;    }
    public double getAmount() {        return amount;    }
    public double getBalance() { return balance;    }
    public Date getDatetime() { return datetime;    }
    public double getMaxWithdrawalAmount() { return maxWithdrawalAmount;    }
    public Currency getCurrency() { return currency;    }
    public String getStatus() { return status;          }
}


