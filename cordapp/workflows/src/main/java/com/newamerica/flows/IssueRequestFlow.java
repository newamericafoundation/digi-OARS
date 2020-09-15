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
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
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
                String externalAccountId,
                String purpose,
                BigDecimal amount,
                Currency currency,
                ZonedDateTime createDatetime,
                ZonedDateTime updateDatetime,
                UniqueIdentifier fundStateLinearId,
                List<AbstractParty> participants
        ) {
            this.outputRequestState = new RequestState(
                    authorizedUserUsername,
                    authorizedUserDept,
                    new LinkedHashMap<>(),
                    Collections.emptyList(),
                    externalAccountId,
                    purpose,
                    amount,
                    currency,
                    createDatetime,
                    updateDatetime,
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
            outputRequestState = outputRequestState.updateAuthorizedPartiesList(inputStateRefFundState.getRequiredSigners());

            if (outputRequestState.amount.compareTo(inputStateRefFundState.maxWithdrawalAmount) > 0) {
                outputRequestState = outputRequestState.changeStatus(RequestState.RequestStateStatus.FLAGGED);
            }
            outputRequestState = outputRequestState.updateParticipantList(inputStateRefFundState.getParticipants());
            outputRequestState = outputRequestState.updateAuthorizedPartiesList(inputStateRefFundState.getRequiredSigners());

            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new RequestContract.Commands.Issue();
            transactionBuilder.addCommand(commandData, inputStateRefFundState.getRequiredSigners().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addOutputState(outputRequestState, RequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            SignedTransaction stx = subFlow(new CollectSignaturesInitiatingFlow(partSignedTx, inputStateRefFundState.getRequiredSigners()));

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputRequestState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> otherPartiesFlowSessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            return subFlow(new FinalityFlow(stx, otherPartiesFlowSessions));
        }
    }

    // Call receiveFinalityFlow for all participants
    @InitiatedBy(IssueRequestFlow.InitiatorFlow.class)
    public static class ExtraInitiatingFlowResponder extends FlowLogic<SignedTransaction> {
        private FlowSession session;
        public ExtraInitiatingFlowResponder(
                FlowSession session
        ){
            this.session = session;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // save the transaction and nothing else
            return subFlow(new ReceiveFinalityFlow(session));
        }
    }

    // Collected the signatures of only the requiredSigners listed on the FundState
    @InitiatingFlow
    @StartableByRPC
    public static class CollectSignaturesInitiatingFlow extends FlowLogic<SignedTransaction> {

        private SignedTransaction transaction;
        private List<AbstractParty> requiredSigners;

        public CollectSignaturesInitiatingFlow(
                SignedTransaction transaction,
                List<AbstractParty> requiredSigners
        ) {
            this.transaction = transaction;
            this.requiredSigners = requiredSigners;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            // create new sessions to signers and trigger the signing responder flow
            List<FlowSession> requiredSigsFlowSessions = requiredSigners.stream().map(i -> initiateFlow(i)).collect(Collectors.toList());
            return subFlow(new CollectSignaturesFlow(transaction, requiredSigsFlowSessions));
        }
    }

    //create sessions for each of the signers
    @InitiatedBy(CollectSignaturesInitiatingFlow.class)
    public static class CollectSignaturesResponder extends FlowLogic<SignedTransaction> {
        private FlowSession session;

        public CollectSignaturesResponder(
                FlowSession session
        ){
            this.session = session;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            return subFlow(new SignTransactionFlow(session) {
                @Override
                protected void checkTransaction(@NotNull SignedTransaction stx) throws FlowException {

                }
            });
        }
    }
}
