package com.newamerica.states;

import com.newamerica.contracts.RequestContract;
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
import java.util.Currency;
import java.util.List;
import java.util.Map;

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
 *  fundStateLinearId -  A reference to the fund state that this request is based on
 */
@BelongsToContract(RequestContract.class)
public class RequestState implements LinearState, Comparable<RequestState> {

    public final BigDecimal maxWithdrawalAmount;
    public final String authorizedUserUsername;
    public final String authorizedUserDept;
    public final Map<String,String> authorizerUserDeptAndUsername;
    public final List<AbstractParty> authorizedParties;
    public final String externalAccountId;
    public final String purpose;
    public final BigDecimal amount;
    public final Currency currency;
    public final ZonedDateTime createDatetime;
    public final ZonedDateTime updateDatetime;
    public final RequestStateStatus status;
    public final UniqueIdentifier fundStateLinearId;
    public final UniqueIdentifier linearId;
    public final List<AbstractParty> participants;

    @ConstructorForDeserialization
    public RequestState(BigDecimal maxWithdrawalAmount,
                        String authorizedUserUsername,
                        String authorizedUserDept,
                        Map<String,String> authorizerUserDeptAndUsername,
                        List<AbstractParty> authorizedParties,
                        String externalAccountId,
                        String purpose,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime createDatetime,
                        ZonedDateTime updateDatetime,
                        RequestStateStatus status,
                        UniqueIdentifier fundStateLinearId,
                        UniqueIdentifier linearId,
                        List<AbstractParty> participants) {
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.authorizedUserUsername = authorizedUserUsername;
        this.authorizedUserDept = authorizedUserDept;
        this.authorizerUserDeptAndUsername = authorizerUserDeptAndUsername;
        this.authorizedParties = authorizedParties;
        this.externalAccountId = externalAccountId;
        this.purpose = purpose;
        this.amount = amount;
        this.currency = currency;
        this.createDatetime = createDatetime;
        this.updateDatetime = updateDatetime;
        this.status = status;
        this.fundStateLinearId = fundStateLinearId;
        this.linearId = linearId;
        this.participants = participants;
    }

    public RequestState(BigDecimal maxWithdrawalAmount,
                        String authorizedUserUsername,
                        String authorizedUserDept,
                        Map<String,String> authorizerUserDeptAndUsername,
                        List<AbstractParty> authorizedParties,
                        String externalAccount,
                        String purpose,
                        BigDecimal amount,
                        Currency currency,
                        ZonedDateTime createDatetime,
                        ZonedDateTime updateDatetime,
                        RequestStateStatus status,
                        UniqueIdentifier fundStateLinearId,
                        List<AbstractParty> participants) {
        this(maxWithdrawalAmount, authorizedUserUsername, authorizedUserDept, authorizerUserDeptAndUsername, authorizedParties, externalAccount, purpose, amount, currency, createDatetime, updateDatetime, status, fundStateLinearId, new UniqueIdentifier(), participants);
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
    public List<AbstractParty> getAuthorizedParties() { return authorizedParties; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public RequestStateStatus getStatus() { return status; }
    public UniqueIdentifier getFundStateLinearId() { return fundStateLinearId; }
    public String getExternalAccountId() { return externalAccountId; }
    public String getPurpose() { return purpose; }
    public ZonedDateTime getCreateDatetime() { return createDatetime; }
    public ZonedDateTime getUpdateDatetime() { return updateDatetime; }
    public Map<String, String> getAuthorizerUserDeptAndUsername() { return authorizerUserDeptAndUsername; }
    public BigDecimal getMaxWithdrawalAmount() { return maxWithdrawalAmount; }


    //helper functions
    public RequestState setMaxWithdrawalLimit(BigDecimal max){
        return new RequestState(
                max,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                this.updateDatetime,
                this.status,
                this.fundStateLinearId,
                this.linearId,
                this.participants
        );
    }
    //helper functions
    public RequestState changeStatus(RequestStateStatus newStatus){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                this.updateDatetime,
                newStatus,
                this.fundStateLinearId,
                this.linearId,
                this.participants
        );
    }
    public RequestState updateParticipantList(List<AbstractParty> participantList){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                this.updateDatetime,
                this.status,
                this.fundStateLinearId,
                this.linearId,
                participantList
        );
    }

    public RequestState updateAuthorizedPartiesList(List<AbstractParty> authorizedParties){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserDeptAndUsername,
                authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                this.updateDatetime,
                this.status,
                this.fundStateLinearId,
                this.linearId,
                this.participants
        );
    }

    public RequestState updateFundStateID(UniqueIdentifier fundStateLinearId){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                this.authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                this.updateDatetime,
                this.status,
                fundStateLinearId,
                this.linearId,
                this.participants
        );
    }

    public RequestState update(Map<String, String> authorizerUserDeptAndUsername, ZonedDateTime updateDatetime){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                updateDatetime,
                this.status,
                this.fundStateLinearId,
                this.linearId,
                this.participants
        );
    }

    public RequestState updateUpdateDatetime(ZonedDateTime updateDatetime){
        return new RequestState(
                this.maxWithdrawalAmount,
                this.authorizedUserUsername,
                this.authorizedUserDept,
                authorizerUserDeptAndUsername,
                this.authorizedParties,
                this.externalAccountId,
                this.purpose,
                this.amount,
                this.currency,
                this.createDatetime,
                updateDatetime,
                this.status,
                this.fundStateLinearId,
                this.linearId,
                this.participants
        );
    }

    @Override
    public int compareTo(RequestState a) {
        return (-1)*(getCreateDatetime().compareTo(a.getCreateDatetime()));
    }

    @CordaSerializable
    public enum RequestStateStatus {
        PENDING("PENDING"),
        FLAGGED("FLAGGED"),
        APPROVED("APPROVED"),
        REJECTED("REJECTED"),
        TRANSFERRED("TRANSFERRED");

        public final String status;
        RequestStateStatus(String status) {
            this.status = status;
        }
    }



}
