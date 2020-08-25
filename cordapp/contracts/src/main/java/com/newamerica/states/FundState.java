package com.newamerica.states;

import com.newamerica.contracts.FundContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * A FundState is an on-ledger representation of off-ledger recovered asLists.
 *
 *  originCountry - the source country which will be sending the funds (US).
 *  receivingCountry - the country which will be receiving the funds (Catan).
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
    public final Party originCountry;
    public final Party receivingCountry;
    public final List<Party> owners;
    public final List<Party> requiredSigners;
    public final BigDecimal amount;
    public final BigDecimal balance;
    public final ZonedDateTime datetime;
    public final BigDecimal maxWithdrawalAmount;
    public final Currency currency;
    public final FundStateStatus status;
    public final UniqueIdentifier linearId;
    public final List<Party> participants;

    @ConstructorForDeserialization
    public FundState(Party originCountry, Party receivingCountry, List<Party> owners, List<Party> requiredSigners, BigDecimal amount, BigDecimal balance, ZonedDateTime datetime, BigDecimal maxWithdrawalAmount, Currency currency, FundStateStatus status, List<Party> participants, UniqueIdentifier linearId) {
        this.originCountry = originCountry;
        this.receivingCountry = receivingCountry;
        this.owners = owners;
        this.requiredSigners = requiredSigners;
        this.amount = amount;
        this.balance = balance;
        this.datetime = datetime;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.currency = currency;
        this.status = status;
        this.participants = participants;
        this.linearId = linearId;
    }

    public FundState(Party originCountry,
                     Party receivingCountry,
                     List<Party> owners,
                     List<Party> requiredSigners,
                     BigDecimal amount,
                     BigDecimal balance,
                     ZonedDateTime datetime,
                     BigDecimal maxWithdrawalAmount,
                     Currency currency,
                     FundStateStatus status,
                     List<Party> participants){
        this(originCountry, receivingCountry, owners, requiredSigners, amount, balance, datetime, maxWithdrawalAmount, currency, status, participants, new UniqueIdentifier());
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
    public Party getOriginCountry() {        return originCountry;    }
    public Party getReceivingCountry() {        return receivingCountry;    }
    public List<Party> getOwners(){ return owners; }
    public List<Party> getRequiredSigners(){ return requiredSigners; }
    public BigDecimal getAmount() {        return amount;    }
    public BigDecimal getBalance() { return balance;    }
    public ZonedDateTime getDatetime() { return datetime;    }
    public BigDecimal getMaxWithdrawalAmount() { return maxWithdrawalAmount;    }
    public Currency getCurrency() { return currency;    }
    public FundStateStatus getStatus() { return status;          }

    //helper functions

    // return the difference between the balance and the withdrawn amount.
    public FundState withdraw(BigDecimal withdrawalAmount){
        return new FundState(
                this.originCountry,
                this.receivingCountry,
                this.owners,
                this.requiredSigners,
                this.amount,
                this.balance.subtract(withdrawalAmount),
                this.datetime,
                this.maxWithdrawalAmount,
                this.currency,
                this.status,
                this.participants
        );
    }

    public enum FundStateStatus{
        ISSUED("issued"),
        RECEIVED("received"),
        PAID("paid");

        public final String status;
        FundStateStatus(String status) {
            this.status = status;
        }
    }
}


