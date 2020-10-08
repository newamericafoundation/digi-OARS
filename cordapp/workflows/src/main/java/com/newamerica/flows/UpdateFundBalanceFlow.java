package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.FundContract;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class UpdateFundBalanceFlow {

    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignedTransaction> {
        private final RequestState requestState;

        public InitiatorFlow(
                RequestState requestState
        ){
            this.requestState = requestState;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            List<UUID> fundStateLinearIdList = new ArrayList<>();
            fundStateLinearIdList.add(requestState.getFundStateLinearId().getId());

            //get StatAndRef for the respective FundState
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, fundStateLinearIdList);
            Vault.Page results = getServiceHub().getVaultService().queryBy(FundState.class, queryCriteria);
            StateAndRef inputStateRef = (StateAndRef) results.getStates().get(0);
            FundState inputStateRefFundState = (FundState) inputStateRef.getState().getData();
            
            if(requestState.getAmount().compareTo(inputStateRefFundState.getBalance()) > 0) {
                throw new IllegalArgumentException("withdrawal amount is bigger than the balance.");
            }

            //create new output state for the fundState
            FundState outputFundState = inputStateRefFundState.withdraw(requestState.getAmount());
            if(outputFundState.getBalance().compareTo(BigDecimal.ZERO) == 0){
                outputFundState = outputFundState.changeStatus(FundState.FundStateStatus.PAID);
            }

            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new FundContract.Commands.Withdraw();
            transactionBuilder.addCommand(commandData, outputFundState.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList()));
            transactionBuilder.addInputState(inputStateRef);
            transactionBuilder.addOutputState(outputFundState, FundContract.ID);
            transactionBuilder.verify(getServiceHub());

            //partially sign transaction
            SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(transactionBuilder, getOurIdentity().getOwningKey());

            //create list of all parties minus ourIdentity for required signatures
            List<Party> otherParties = outputFundState.getParticipants().stream().map(i -> ((Party) i)).collect(Collectors.toList());
            otherParties.remove(getOurIdentity());

            //create sessions based on otherParties
            List<FlowSession> flowSessions = otherParties.stream().map(this::initiateFlow).collect(Collectors.toList());

            SignedTransaction finalizedTransaction = subFlow(new FinalityFlow(subFlow(new CollectSignaturesFlow(partSignedTx, flowSessions)), flowSessions));
            subFlow( new IssuePartialRequestFundFlow.InitiatorFlow(
                    requestState.getAuthorizedUserDept(),
                    requestState.getAuthorizedParties(),
                    requestState.getAmount(),
                    requestState.getCurrency(),
                    requestState.getUpdateDatetime(),
                    requestState.getPurpose(),
                    outputFundState.getLinearId(),
                    outputFundState.getPartialRequestParticipants()
                    )
            );
            return finalizedTransaction;
        }
    }

    /**
     * This is the flow which signs FundState updates.
     */

    @InitiatedBy(UpdateFundBalanceFlow.InitiatorFlow.class)
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
