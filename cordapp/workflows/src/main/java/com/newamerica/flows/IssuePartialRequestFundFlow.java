package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.PartialRequestContract;
import com.newamerica.states.PartialRequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class IssuePartialRequestFundFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private PartialRequestState outputPartialRequestState;

        public InitiatorFlow(
                String authorizedUserDept,
                Party authorizerDept,
                String externalAccountId,
                BigDecimal amount,
                Currency currency,
                ZonedDateTime datetime,
                UniqueIdentifier fundStateLinearId,
                List<AbstractParty> participants
        ){
            this.outputPartialRequestState = new PartialRequestState(
                    authorizedUserDept,
                    authorizerDept,
                    amount,
                    currency,
                    datetime,
                    fundStateLinearId,
                    participants
            );
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new PartialRequestContract.Commands.Issue();
            outputPartialRequestState.getParticipants().add(getOurIdentity());
            transactionBuilder.addCommand(commandData, outputPartialRequestState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addOutputState(outputPartialRequestState, PartialRequestContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputPartialRequestState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> flowSessions = otherParties.stream().map(i -> initiateFlow(i)).collect(Collectors.toList());

            SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, flowSessions));
            return subFlow(new FinalityFlow(signedTransaction, flowSessions));
        }
    }

    /**
     * This is the flow which signs PartialRequestState issuances.
     */

    @InitiatedBy(IssuePartialRequestFundFlow.InitiatorFlow.class)
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
                        req.using("This must be an PartialRequestState transaction", output instanceof PartialRequestState);
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
