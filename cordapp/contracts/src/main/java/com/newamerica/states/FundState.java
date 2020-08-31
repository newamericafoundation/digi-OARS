package com.newamerica.states;

import com.newamerica.contracts.FundContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * A FundState is an on-ledger representation of off-ledger recovered asLists.
 *
 *  originParty - the source country which will be sending the funds (US).
 *  receivingParty - the country which will be receiving the funds (Catan).
 *  amount - the original value the fund held when it was issued.
 *  balance - the current value that exists in the fund.
 *  datetime - the day/time the fund was issued.
 *  maxWithdrawalAmount - a term agreed upon in an off-leger bi-lateral agreement.
 *  currency - the globally recognized currency for the fund balance and amount.
 *  status - current stage of the fundState's lifecycle in Corda (can be ISSUED or PAID)
 *  isRecieved - a flag modified by an authorized treasury user
 */

@BelongsToContract(FundContract.class)
public class FundState implements LinearState {
    public final Party originParty;
    public final Party receivingParty;
    public final List<AbstractParty> owners;
    public final List<AbstractParty> requiredSigners;
    public final List<AbstractParty> partialRequestParticipants;
    public final BigDecimal amount;
    public final BigDecimal balance;
    public final ZonedDateTime datetime;
    public final BigDecimal maxWithdrawalAmount;
    public final Currency currency;
    public final FundStateStatus status;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;

    @ConstructorForDeserialization
    public FundState(Party originParty, Party receivingParty, List<AbstractParty> owners, List<AbstractParty> requiredSigners, List<AbstractParty> partialRequestParticipants, BigDecimal amount, BigDecimal balance, ZonedDateTime datetime, BigDecimal maxWithdrawalAmount, Currency currency, FundStateStatus status, List<AbstractParty> participants, UniqueIdentifier linearId) {
        this.originParty = originParty;
        this.receivingParty = receivingParty;
        this.owners = owners;
        this.requiredSigners = requiredSigners;
        this.partialRequestParticipants = partialRequestParticipants;
        this.amount = amount;
        this.balance = balance;
        this.datetime = datetime;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.currency = currency;
        this.status = status;
        this.participants = participants;
        this.linearId = linearId;
    }

    public FundState(Party originParty,
                     Party receivingParty,
                     List<AbstractParty> owners,
                     List<AbstractParty> requiredSigners,
                     List<AbstractParty> partialRequestParticipants,
                     BigDecimal amount,
                     BigDecimal balance,
                     ZonedDateTime datetime,
                     BigDecimal maxWithdrawalAmount,
                     Currency currency,
                     FundStateStatus status,
                     List<AbstractParty> participants){
        this(originParty, receivingParty, owners, requiredSigners, partialRequestParticipants, amount, balance, datetime, maxWithdrawalAmount, currency, status, participants, new UniqueIdentifier());
    }

    //getters
    @Override
    public List<AbstractParty> getParticipants() {
        return new ArrayList<>(this.participants);
    }
    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }
    public List<AbstractParty> getOwners(){ return owners; }
    public List<AbstractParty> getRequiredSigners(){ return requiredSigners; }
    public List<AbstractParty> getPartialRequestParticipants(){ return partialRequestParticipants; }
    public BigDecimal getAmount() {        return amount;    }
    public BigDecimal getBalance() { return balance;    }
    public ZonedDateTime getDatetime() { return datetime;    }
    public BigDecimal getMaxWithdrawalAmount() { return maxWithdrawalAmount;    }
    public Currency getCurrency() { return currency;    }
    public FundStateStatus getStatus() { return status;          }
    public Party getOriginParty() { return originParty; }
    public Party getReceivingParty() { return receivingParty; }



    //helper functions

    // return the difference between the balance and the withdrawn amount.
    public FundState withdraw(BigDecimal withdrawalAmount){
        return new FundState(
                this.originParty,
                this.receivingParty,
                this.owners,
                this.requiredSigners,
                this.partialRequestParticipants,
                this.amount,
                this.balance.subtract(withdrawalAmount),
                this.datetime,
                this.maxWithdrawalAmount,
                this.currency,
                this.status,
                this.participants
        );
    }

    public FundState changeStatus(FundStateStatus newStatus){
        return new FundState(
                this.originParty,
                this.receivingParty,
                this.owners,
                this.requiredSigners,
                this.partialRequestParticipants,
                this.amount,
                this.balance,
                this.datetime,
                this.maxWithdrawalAmount,
                this.currency,
                newStatus,
                this.participants
        );
    }

    @CordaSerializable
    public enum FundStateStatus{
        ISSUED("ISSUED"),
        RECEIVED("RECEIVED"),
        PAID("RECEIVED");

        public final String status;
        FundStateStatus(String status) {
            this.status = status;
        }
    }
}


