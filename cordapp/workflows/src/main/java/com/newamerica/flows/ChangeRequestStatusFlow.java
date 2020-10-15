package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.RequestContract;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ChangeRequestStatusFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private final StateAndRef inputRequestStateAndRef;

        public InitiatorFlow(StateAndRef inputRequestStateAndRef) {
            this.inputRequestStateAndRef = inputRequestStateAndRef;
        }
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            RequestState inputRequestState = (RequestState) inputRequestStateAndRef.getState().getData();
            RequestState outputRequestState = inputRequestState
                    .changeStatus(RequestState.RequestStateStatus.TRANSFERRED)
                    .updateUpdateDatetime(ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC")));
            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new RequestContract.Commands.Transfer();
            transactionBuilder.addCommand(commandData, outputRequestState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addInputState(inputRequestStateAndRef);
            transactionBuilder.addOutputState(outputRequestState, RequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputRequestState
                    .getParticipants()
                    .stream()
                    .map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> flowSessions = otherParties.stream().map(i -> initiateFlow(i)).collect(Collectors.toList());

            SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, flowSessions));
            return subFlow(new FinalityFlow(signedTransaction, flowSessions));
        }
    }

    @InitiatedBy(ChangeRequestStatusFlow.InitiatorFlow.class)
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
