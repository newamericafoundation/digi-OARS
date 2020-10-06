package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.RequestContract;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;

import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
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
}
