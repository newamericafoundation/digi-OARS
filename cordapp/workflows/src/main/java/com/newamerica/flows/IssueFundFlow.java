package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.FundContract;
import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This flow is responsible for the issuance of FundState on ledger.
 */

public class IssueFundFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction>{
        private final FundState outputFundState;

        public InitiatorFlow(Party originCountry, Party targetCountry, Set<Party> owners, Set<Party> requiredSigners, BigDecimal amount, BigDecimal balance, ZonedDateTime datetime, BigDecimal maxWithdrawalAmount, Currency currency, Set participants){
            this.outputFundState = new FundState(originCountry,targetCountry, owners, requiredSigners, amount, balance, datetime, maxWithdrawalAmount, currency, FundState.FundStateStatus.ISSUED, false, participants);
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new FundContract.Commands.Issue();
            outputFundState.getParticipants().add(getOurIdentity());
            transactionBuilder.addCommand(commandData, outputFundState.getParticipants().stream().map(i -> (i.getOwningKey())).collect(Collectors.toList()));
            transactionBuilder.addOutputState(outputFundState, FundContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputFundState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> flowSessions = otherParties.stream().map(i -> initiateFlow(i)).collect(Collectors.toList());

            SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(partSignedTx, flowSessions));
            return subFlow(new FinalityFlow(signedTransaction, flowSessions));
        }
    }

    /**
     * This is the flow which signs FundState issuances.
     */

    @InitiatedBy(IssueFundFlow.InitiatorFlow.class)
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
                        req.using("This must be an FundState transaction", output instanceof FundState);
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
