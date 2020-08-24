package com.newamerica.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.newamerica.contracts.FundContract;
import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.TransactionBuilder;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.List;

import static com.newamerica.flows.CordappConfigUtilities.getPreferredNotary;

/**
 * This flow is responsible for the issuance of FundState on ledger.
 */

public class IssueFundFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class InitiatorFlow extends FlowLogic<SignTransactionFlow>{
//        private final Party originCountry;
//        private final Party targetCountry;
//        private final List<Party> owners;
//        private final List<Party> requiredSigners;
//        private final double amount;
//        private final double balance;
//        private final ZonedDateTime datetime;
//        private final double maxWithdrawalAmount;
//        private final Currency currency;
//        private final FundState.FundStateStatus status;
        private final FundState outputFundState;

        public InitiatorFlow(Party originCountry, Party targetCountry, List<Party> owners, List<Party> requiredSigners, double amount, double balance, ZonedDateTime datetime, double maxWithdrawalAmount, Currency currency){
            this.outputFundState = new FundState(originCountry,targetCountry, owners, requiredSigners, amount, balance, datetime, maxWithdrawalAmount, currency, FundState.FundStateStatus.ISSUED);
        }

        @Suspendable
        @Override
        public SignTransactionFlow call() throws FlowException {
            final Party notary = getPreferredNotary(getServiceHub());
            TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
            CommandData commandData = new FundContract.Commands.Issue();
            transactionBuilder.addCommand(commandData, outputFundState.getParticipants().add(getOurIdentity())

            return null;
        }
    }
}
