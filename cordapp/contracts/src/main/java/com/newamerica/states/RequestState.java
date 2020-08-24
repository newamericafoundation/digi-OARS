package com.newamerica.states;

import com.newamerica.contracts.RequestContract;
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
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

/**
 * A RequestState is an on-ledger representation of request data that gets stored in the database.
 *
 *  authorizedUserUsername - The name of a Catan MoFA approved authorized user, who sends the request
 *  authorizedUserDept - the department name that the authorized user belongs to
 *  authorizerUsername - The name of a Catan MoJ authorizer, who sign off the request
 *  authorizerDept - the current value that exists in the fund.
 *  externalAccount - the external account name/id that the fund transfers to
 *  datetime - the day/time the fund was issued.
 *  currency - the globally recognized currency for the fund balance and amount.
 *  status - current stage of the requestState's lifecycle in Corda (can be ISSUED, PENDING, FlAGGED)
 *  fundStateRef -  A reference to the fund state that this request is based on
 */
@BelongsToContract(RequestContract.class)
public class RequestState implements LinearState {
    public final String authorizedUserUsername;
    public final String authorizedUserDept;
    public final String authorizerUserUsername;
    public final Party authorizerDept;
    public final String externalAccountId;
    public final BigDecimal amount;
    public final Currency currency;
    public final ZonedDateTime datetime;
    public final RequestStateStatus status;
    public final StateAndRef<FundState> fundStateRef;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;

    @ConstructorForDeserialization
    public RequestState(String authorizedUserUsername,
                        String authorizedUserDept,
                        String authorizerUserUsername,
                        Party authorizerDept,
                        String externalAccountId,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime datetime,
                        RequestStateStatus status,
                        StateAndRef<FundState> fundStateRef,
                        UniqueIdentifier linearId,
                        List<AbstractParty> participants) {
        this.authorizedUserUsername = authorizedUserUsername;
        this.authorizedUserDept = authorizedUserDept;
        this.authorizerUserUsername = authorizerUserUsername;
        this.authorizerDept = authorizerDept;
        this.externalAccountId = externalAccountId;
        this.amount = amount;
        this.currency = currency;
        this.datetime = datetime;
        this.status = status;
        this.fundStateRef = fundStateRef;
        this.linearId = linearId;
        this.participants = participants;
    }

    public RequestState(String authorizedUserUsername,
                        String authorizedUserDept,
                        String authorizerUsername,
                        Party authorizerDept,
                        String externalAccount,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime datetime,
                        RequestStateStatus status,
                        StateAndRef<FundState> fundStateRef,
                        List<AbstractParty> participants) {
        this(authorizedUserUsername, authorizedUserDept, authorizerUsername, authorizerDept, externalAccount, amount, currency, datetime, status, fundStateRef,new UniqueIdentifier(), participants);
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() { return this.linearId; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() { return participants;}


    public String getAuthorizedUserUsername() { return authorizedUserUsername; }
    public String getAuthorizedUserDept() { return authorizedUserDept; }
    public Party getAuthorizerDept() { return authorizerDept; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public ZonedDateTime getDatetime() { return datetime; }
    public RequestStateStatus getStatus() { return status; }
    public StateAndRef<FundState> getFundStateRef() { return fundStateRef; }
    public String getAuthorizerUserUsername() { return authorizerUserUsername; }
    public String getExternalAccountId() { return externalAccountId; }


    public enum RequestStateStatus{
        ISSUED,
        PENDING,
        FLAGGED
    }
}