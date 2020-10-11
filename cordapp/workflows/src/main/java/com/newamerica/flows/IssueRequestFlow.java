package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.RequestContract;
import com.newamerica.states.ConfigState;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;

import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

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
                    null,
                    participants
            );
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            //get lastest config
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<ConfigState>> configs = getServiceHub().getVaultService().queryBy(ConfigState.class, queryCriteria).getStates();
            ConfigState lastestConfig = configs.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(ConfigState::getCreateDatetime).reversed()).collect(Collectors.toList()).get(0);

            // if request amount > max limit, then flag this request
            if (outputRequestState.getAmount().compareTo(lastestConfig.getMaxWithdrawalAmount()) > 0) {
                outputRequestState.changeStatus(RequestState.RequestStateStatus.FLAGGED);
            }

            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new RequestContract.Commands.Issue();
            transactionBuilder.addCommand(commandData, outputRequestState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addOutputState(outputRequestState, RequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction by ourself
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputRequestState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> otherPartiesFlowSessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());
            SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, otherPartiesFlowSessions));
            return subFlow(new FinalityFlow(signedTransaction, otherPartiesFlowSessions));
        }
    }

    @InitiatedBy(IssueRequestFlow.InitiatorFlow.class)
    public static class ResponderFlow extends FlowLogic<SignedTransaction>{
        private final FlowSession flowSession;
        private SecureHash txWeJustSigned;

        public ResponderFlow(FlowSession flowSession){
            this.flowSession = flowSession;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            class SignTxFlow extends SignTransactionFlow{

                private SignTxFlow(FlowSession flowSession, ProgressTracker progressTracker){
                    super(flowSession, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx){
                    requireThat(req -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        req.using("This must be an RequestState transaction", output instanceof RequestState);
                        return null;
                    });
                    txWeJustSigned = stx.getId();
                }
            }
            flowSession.getCounterpartyFlowInfo().getFlowVersion();

            // Create a sign transaction flow
            SignTxFlow signTxFlow = new SignTxFlow(flowSession, SignTransactionFlow.Companion.tracker());

            // Run the sign transaction flow to sign the transaction
            subFlow(signTxFlow);
            return subFlow(new ReceiveFinalityFlow(flowSession, txWeJustSigned));
        }
    }
}
