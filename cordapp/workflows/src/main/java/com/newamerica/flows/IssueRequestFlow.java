package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.RequestContract;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;

public class IssueRequestFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private RequestState outputRequestState;

        public InitiatorFlow(
                String authorizedUserUsername,
                String authorizedUserDept,
                String authorizerUserUsername,
                Party authorizerDept,
                String externalAcountId,
                BigDecimal amount,
                Currency currency,
                ZonedDateTime datetime,
                UniqueIdentifier fundStateLinearId,
                List<AbstractParty> participants
        ){
            this.outputRequestState = new RequestState(
                    authorizedUserUsername,
                    authorizedUserDept,
                    authorizerUserUsername,
                    authorizerDept,
                    externalAcountId,
                    amount,
                    currency,
                    datetime,
                    RequestState.RequestStateStatus.PENDING,
                    fundStateLinearId,
                    participants
            );
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            List<UUID> fundStateLinearIdList = new ArrayList<>();
            fundStateLinearIdList.add(outputRequestState.fundStateLinearId.getId());

            //get StatAndRef for the respective FundState
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, fundStateLinearIdList);
            Vault.Page results = getServiceHub().getVaultService().queryBy(FundState.class, queryCriteria);
            StateAndRef inputStateRef = (StateAndRef) results.getStates().get(0);
            FundState inputStateRefFundState = (FundState) inputStateRef.getState().getData();
            if (outputRequestState.amount.compareTo(inputStateRefFundState.maxWithdrawalAmount) > 0){
                outputRequestState = outputRequestState.changeStatus(RequestState.RequestStateStatus.FLAGGED);
            }

            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new RequestContract.Commands.Issue();
            outputRequestState.getParticipants().add(getOurIdentity());
            transactionBuilder.addCommand(commandData, outputRequestState.getParticipants().stream().map(i -> (i.getOwningKey())).collect(Collectors.toList()));
            transactionBuilder.addOutputState(outputRequestState, RequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = inputStateRefFundState.getRequiredSigners().stream().map(i -> ((Party) i)).collect(Collectors.toList());

            //create sessions based on otherParties
            List<FlowSession> flowSessions = otherParties.stream().map(i -> initiateFlow(i)).collect(Collectors.toList());

            SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, flowSessions));
            return subFlow(new FinalityFlow(signedTransaction, flowSessions));
        }
    }
}
