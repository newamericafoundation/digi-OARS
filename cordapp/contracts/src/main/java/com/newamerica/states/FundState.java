package com.newamerica.states;

import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/**
 * A FundState is an on-ledger representation of off-ledger recovered assets.
 *
 *  originCountry - the source country which will be sending the funds (US).
 *  targetCountry - the target country which will be receiving the funds (Catan).
 *  amount - the original value the fund held when it was issued.
 *  balance - the current value that exists in the fund.
 *  datetime - the day/time the fund was issued.
 *  maxWithdrawalAmount - a term agreed upon in an off-leger bi-lateral agreement.
 *  currency - the globally recognized currency for the fund balance and amount.
 *  status - current stage of the fundState's lifecycle in Corda (can be ISSUED or PAID)
 */

@BelongsToContract(com.newamerica.contracts.TemplateContract.class)
public class FundState implements LinearState {
    public final Party originCountry;
    public final Party targetCountry;
    public final List<Party> owners;
    public final List<Party> requiredSigners;
    public final double amount;
    public final double balance;
    public final ZonedDateTime datetime;
    public final double maxWithdrawalAmount;
    public final Currency currency;
    public final FundStateStatus status;
    public final UniqueIdentifier linearId;

    @ConstructorForDeserialization
    public FundState(Party originCountry, Party targetCountry, List<Party> owners, List<Party> requiredSigners, double amount, double balance, ZonedDateTime datetime, double maxWithdrawalAmount, Currency currency, FundStateStatus status, UniqueIdentifier linearId) {
        this.originCountry = originCountry;
        this.targetCountry = targetCountry;
        this.owners = owners;
        this.requiredSigners = requiredSigners;
        this.amount = amount;
        this.balance = balance;
        this.datetime = datetime;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.currency = currency;
        this.status = status;
        this.linearId = linearId;
    }

    public FundState(Party originCountry, Party targetCountry, List<Party> owners, List<Party> requiredSigners, double amount, double balance, ZonedDateTime datetime, double maxWithdrawalAmount, Currency currency, FundStateStatus status){
        this(originCountry, targetCountry, owners, requiredSigners, amount, balance, datetime, maxWithdrawalAmount, currency, status, new UniqueIdentifier());
    }

    //getters
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList();
    }
    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }
    public Party getOriginCountry() {        return originCountry;    }
    public Party getTargetCountry() {        return targetCountry;    }
    public List<Party> getOwners(){ return owners; }
    public List<Party> getRequiredSigners(){ return requiredSigners; }
    public double getAmount() {        return amount;    }
    public double getBalance() { return balance;    }
    public ZonedDateTime getDatetime() { return datetime;    }
    public double getMaxWithdrawalAmount() { return maxWithdrawalAmount;    }
    public Currency getCurrency() { return currency;    }
    public FundStateStatus getStatus() { return status;          }

    //helper functions

    // return the difference between the balance and the withdrawn amount.
    public FundState withdraw(double withdrawalAmount){
        return new FundState(
                this.originCountry,
                this.targetCountry,
                this.owners,
                this.requiredSigners,
                this.amount,
                (this.balance - withdrawalAmount),
                this.datetime,
                this.maxWithdrawalAmount,
                this.currency,
                this.status
        );
    }

    public enum FundStateStatus{
        ISSUED,
        PAID
    }
}


