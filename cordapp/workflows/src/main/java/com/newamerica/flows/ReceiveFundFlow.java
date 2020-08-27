package com.newamerica.flows;


import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.FundContract;
import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This flow is responsible for the receiving FundState on ledger.
 */

public class ReceiveFundFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private final UniqueIdentifier fundStateLinearId;

        public InitiatorFlow(UniqueIdentifier fundStateLinearId) {
            this.fundStateLinearId = fundStateLinearId;
        }


        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new FundContract.Commands.Receive();
            // get input fund state
            UUID inputFundStateLinearId = fundStateLinearId.getId();
            QueryCriteria queryCriteria =
                    new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(inputFundStateLinearId));
            Vault.Page results = getServiceHub().getVaultService().queryBy(FundState.class, queryCriteria);
            StateAndRef inputFundStateAndRef = (StateAndRef) results.getStates().get(0);
            FundState inputFundState = (FundState) inputFundStateAndRef.getState().getData();

            // contruct output fund state
            FundState outputFundState = new FundState(
                    inputFundState.getOriginCountry(),
                    inputFundState.getReceivingCountry(),
                    inputFundState.getOwners(),
                    inputFundState.getRequiredSigners(),
                    inputFundState.getPartialRequestParticipants(),
                    inputFundState.getAmount(),
                    inputFundState.getBalance(),
                    inputFundState.getDatetime(),
                    inputFundState.getMaxWithdrawalAmount(),
                    inputFundState.getCurrency(),
                    FundState.FundStateStatus.RECEIVED,
                    inputFundState.getParticipants()
            );

            // build tx
            transactionBuilder.addCommand(
                    commandData,
                    outputFundState.getParticipants().stream().map(i -> (i.getOwningKey())).collect(Collectors.toList()));
            transactionBuilder.addInputState(inputFundStateAndRef);
            transactionBuilder.addOutputState(outputFundState, FundContract.ID);

            //verify and partially sign transaction
            transactionBuilder.verify(getServiceHub());
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputFundState
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


    @InitiatedBy(ReceiveFundFlow.InitiatorFlow.class)
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
