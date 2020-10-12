package com.newamerica.states;


import com.newamerica.contracts.ConfigContract;
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

@BelongsToContract(ConfigContract.class)
public class ConfigState implements LinearState {
    public final String creator;
    public final String country;
    public final BigDecimal maxWithdrawalAmount;
    public final Currency currency;
    public final ZonedDateTime createDatetime;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;


    @ConstructorForDeserialization
    public ConfigState(
                     String creator,
                     String country,
                     BigDecimal maxWithdrawalAmount,
                     Currency currency,
                     ZonedDateTime createDatetime,
                     List<AbstractParty> participants,
                     UniqueIdentifier linearId) {
        this.creator = creator;
        this.country = country;
        this.createDatetime = createDatetime;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.currency = currency;
        this.participants = participants;
        this.linearId = linearId;
    }

    public ConfigState(
            String creator,
            String country,
            BigDecimal maxWithdrawalAmount,
            Currency currency,
            ZonedDateTime createDatetime,
            List<AbstractParty> participants){
        this( creator, country, maxWithdrawalAmount, currency, createDatetime, participants, new UniqueIdentifier());
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return new ArrayList<>(this.participants);
    }
    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }
    public String getCreator() { return creator; }
    public String getCountry() { return country; }
    public BigDecimal getMaxWithdrawalAmount() {return maxWithdrawalAmount; }
    public Currency getCurrency() {return currency; }
    public ZonedDateTime getCreateDatetime() {return createDatetime; }


}
