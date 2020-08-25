package com.newamerica.states;


import com.newamerica.contracts.RequestContract;
import com.newamerica.contracts.TransferContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.StateAndRef;
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
 * A TransferState is an on-ledger representation of a transfer issued by Catan Treasury to the department and user that made the request.
 *
 *  issuanceParty -  Catan Treasury
 *  authorizedUserUsername - The name of a Catan MoFA approved authorized user, who sends the request
 *  receivingDept - the department name that the authorized user belongs to
 *  authorizerDept - the current value that exists in the fund.
 *  externalAccount - the external account name/id that the fund transfers to
 *  datetime - the day/time the fund was issued.
 *  currency - the globally recognized currency for the fund balance and amount.
 *  requestStateRef -  A reference to the request state that this transfer is based on
 */
@BelongsToContract(TransferContract.class)
public class TransferState implements LinearState {
    public final Party issuanceParty;
    public final String receivingDept;
    public final String authorizedUserUsername;
    public final String externalAccountId;
    public final BigDecimal amount;
    public final Currency currency;
    public final ZonedDateTime datetime;
    public final UniqueIdentifier requestStateLinearId;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;

    @ConstructorForDeserialization
    public TransferState(Party issuanceParty,
                         String receivingDept,
                         String authorizedUserUsername,
                         String externalAccountId,
                         BigDecimal amount,
                         Currency currency,
                         ZonedDateTime datetime,
                         UniqueIdentifier requestStateLinearId,
                         UniqueIdentifier linearId,
                         List<AbstractParty> participants) {
        this.issuanceParty = issuanceParty;
        this.receivingDept = receivingDept;
        this.authorizedUserUsername = authorizedUserUsername;
        this.externalAccountId = externalAccountId;
        this.amount = amount;
        this.currency = currency;
        this.datetime = datetime;
        this.requestStateLinearId = requestStateLinearId;
        this.linearId = linearId;
        this.participants = participants;
    }

    public TransferState(Party issuanceParty,
                         String receivingDept,
                         String authorizedUserUsername,
                         String externalAccountId,
                         BigDecimal amount,
                         Currency currency,
                         ZonedDateTime datetime,
                         UniqueIdentifier requestStateLinearId,
                         List<AbstractParty> participants) {
        this(issuanceParty, receivingDept, authorizedUserUsername, externalAccountId, amount, currency, datetime, requestStateLinearId, new UniqueIdentifier(), participants);
    }


    public Party getIssuanceParty() { return issuanceParty; }
    public String getReceivingDept() { return receivingDept; }
    public String getAuthorizedUserUsername() { return authorizedUserUsername; }
    public String getExternalAccountId() { return externalAccountId; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public ZonedDateTime getDatetime() { return datetime; }
    public UniqueIdentifier getRequestStateLinearId() { return requestStateLinearId; }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() { return this.linearId; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() { return participants; }
}
