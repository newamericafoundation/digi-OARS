package com.newamerica.states;

import com.newamerica.contracts.PartialRequestContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

/**
 * A PartialRequestState is an on-ledger representation of partial request data that gets stored in the database.
 *
 *  authorizedUserDept - the department name that the authorized user belongs to
 *  authorizerDept - the current value that exists in the fund.
 *  externalAccount - the external account name/id that the fund transfers to
 *  datetime - the day/time the fund was issued.
 *  currency - the globally recognized currency for the fund balance and amount.
 *  fundStateLinearId -  A reference to the fund state that this request is based on
 */
@BelongsToContract(PartialRequestContract.class)
public class PartialRequestState implements LinearState {

    public final String authorizedUserDept;
    public final Party authorizerDept;
    public final BigDecimal amount;
    public final Currency currency;
    public final ZonedDateTime datetime;
    public final UniqueIdentifier fundStateLinearId;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;

    @ConstructorForDeserialization
    public PartialRequestState(String authorizedUserDept,
                        Party authorizerDept,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime datetime,
                        UniqueIdentifier fundStateLinearId,
                        UniqueIdentifier linearId,
                        List<AbstractParty> participants) {
        this.authorizedUserDept = authorizedUserDept;
        this.authorizerDept = authorizerDept;
        this.amount = amount;
        this.currency = currency;
        this.datetime = datetime;
        this.fundStateLinearId = fundStateLinearId;
        this.linearId = linearId;
        this.participants = participants;
    }

    public PartialRequestState(String authorizedUserDept,
                        Party authorizerDept,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime datetime,
                        UniqueIdentifier fundStateLinearId,
                        List<AbstractParty> participants) {
        this(authorizedUserDept, authorizerDept, amount, currency, datetime, fundStateLinearId, new UniqueIdentifier(), participants);
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() { return this.linearId; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() { return participants;}

    //getters
    public String getAuthorizedUserDept() { return authorizedUserDept; }
    public Party getAuthorizerDept() { return authorizerDept; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public ZonedDateTime getDatetime() { return datetime; }
    public UniqueIdentifier getFundStateLinearId() { return fundStateLinearId; }

}
