package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.RequestContract;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ApproveRequestFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier requestStateLinearId;
        private final String authorizerUserUsername;
        private final String authorizerUserDept;
        private final ZonedDateTime updateDatetime;
        private final UniqueIdentifier fundStateLinearId;


        public InitiatorFlow(UniqueIdentifier requestStateLinearId,
                             String authorizerUserUsername,
                             String authorizerUserDept,
                             ZonedDateTime updateDatetime,
                             UniqueIdentifier fundStateLinearId) {
            this.requestStateLinearId = requestStateLinearId;
            this.authorizerUserUsername = authorizerUserUsername;
            this.authorizerUserDept = authorizerUserDept;
            this.updateDatetime = updateDatetime;
            this.fundStateLinearId = fundStateLinearId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            List<UUID> requestStateLinearIdList = new ArrayList<>();
            requestStateLinearIdList.add(requestStateLinearId.getId());

            //get StatAndRef for the respective RequestState
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, requestStateLinearIdList);
            Vault.Page results = getServiceHub().getVaultService().queryBy(RequestState.class, queryCriteria);
            StateAndRef stateRef = (StateAndRef) results.getStates().get(0);
            RequestState inputRequestState = (RequestState) stateRef.getState().getData();

            //get matched fund state given fundStateId
            List<UUID> fundStateLinearIdList = new ArrayList<>();
            fundStateLinearIdList.add(fundStateLinearId.getId());

            //get StatAndRef for the respective FundState
            QueryCriteria fundQueryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, fundStateLinearIdList);
            Vault.Page fundResults = getServiceHub().getVaultService().queryBy(FundState.class, fundQueryCriteria);
            StateAndRef inputStateRef = (StateAndRef) fundResults.getStates().get(0);
            FundState inputStateRefFundState = (FundState) inputStateRef.getState().getData();

            if(inputStateRefFundState.getStatus() != FundState.FundStateStatus.RECEIVED) {
                throw new IllegalArgumentException("FundState must be in received status!");
            }

            // create output request state
            Map<String, String> authorizerUserDeptAndUsername = new LinkedHashMap<>();
            authorizerUserDeptAndUsername.put(authorizerUserDept, authorizerUserUsername);

            RequestState outputRequestState = inputRequestState
                    .changeStatus(RequestState.RequestStateStatus.APPROVED)
                    .update(authorizerUserDeptAndUsername, updateDatetime)
                    .updateFundStateID(fundStateLinearId)
                    .updateAuthorizedPartiesList(inputStateRefFundState.getAuthorizedParties());

            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new RequestContract.Commands.Approve();
            transactionBuilder.addCommand(commandData, inputStateRefFundState.getAuthorizedParties().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addInputState(stateRef);
            transactionBuilder.addOutputState(outputRequestState, RequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction by ourself
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());
            //collect signature from authorized parties
            SignedTransaction stx = subFlow(new CollectSignaturesInitiatingFlow(partSignedTx, inputStateRefFundState.getAuthorizedParties()));

            //create list of all participants session minus our identity
            List<Party> otherParties = outputRequestState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());
            List<FlowSession> flowSessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            SignedTransaction finalizedTransaction = subFlow(new FinalityFlow(stx, flowSessions));
            RequestState approveRequestState = (RequestState) finalizedTransaction.getTx().getOutputStates().get(0);
            subFlow(new UpdateFundBalanceFlow.InitiatorFlow(
                    approveRequestState
            ));
            return finalizedTransaction;
        }
    }
    /**
     * This is the flow which approves RequestState updates.
     */

    // Call receiveFinalityFlow for all participants
    @InitiatedBy(ApproveRequestFlow.InitiatorFlow.class)
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
