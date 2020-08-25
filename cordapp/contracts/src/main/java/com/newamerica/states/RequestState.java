package com.newamerica.states;

import com.newamerica.contracts.RequestContract;
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
 * A RequestState is an on-ledger representation of request data that gets stored in the database.
 *
 *  authorizedUserUsername - The name of a Catan MoFA approved authorized user, who sends the request
 *  authorizedUserDept - the department name that the authorized user belongs to
 *  authorizerUsername - The name of a Catan MoJ authorizer, who sign off the request
 *  authorizerDept - the current value that exists in the fund.
 *  externalAccount - the external account name/id that the fund transfers to
 *  datetime - the day/time the fund was issued.
 *  currency - the globally recognized currency for the fund balance and amount.
<<<<<<< HEAD
 *  status - current stage of the requestState's lifecycle in Corda (can be ISSUED, PENDING, FlAGGED)
 *  fundStateLinearId -  A reference to the fund state that this request is based on
=======
 *  status - current stage of the requestState's lifecycle in Corda (can be ISSUED, PENDING, FLAGGED)
 *  fundStateRef -  A reference to the fund state that this request is based on
>>>>>>> PNA-35-Create-Request-Flow
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
    public final UniqueIdentifier fundStateLinearId;
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
                        UniqueIdentifier fundStateLinearId,
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
        this.fundStateLinearId = fundStateLinearId;
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
                        UniqueIdentifier fundStateLinearId,
                        List<AbstractParty> participants) {
        this(authorizedUserUsername, authorizedUserDept, authorizerUsername, authorizerDept, externalAccount, amount, currency, datetime, status, fundStateLinearId, new UniqueIdentifier(), participants);
    }


    @NotNull
    @Override
    public UniqueIdentifier getLinearId() { return this.linearId; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() { return participants;}

    //getters
    public String getAuthorizedUserUsername() { return authorizedUserUsername; }
    public String getAuthorizedUserDept() { return authorizedUserDept; }
    public Party getAuthorizerDept() { return authorizerDept; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public ZonedDateTime getDatetime() { return datetime; }
    public RequestStateStatus getStatus() { return status; }
    public UniqueIdentifier getFundStateLinearId() { return fundStateLinearId; }
    public String getAuthorizerUserUsername() { return authorizerUserUsername; }
    public String getExternalAccountId() { return externalAccountId; }

    //helper functions
    public RequestState changeStatus(RequestStateStatus newStatus){
        return new RequestState(
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserUsername,
                this.authorizerDept,
                this.externalAccountId,
                this.amount,
                this.currency,
                this.datetime,
                newStatus,
                this.fundStateLinearId,
                this.participants
        );
    }

    public enum RequestStateStatus {
        PENDING("pending"),
        FLAGGED("flagged"),
        APPROVED("approved");

        public final String status;
        RequestStateStatus(String status) {
            this.status = status;
        }
    }
}
