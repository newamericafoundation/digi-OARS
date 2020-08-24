package com.newamerica.contracts;

import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import java.util.Set;
import java.math.BigDecimal;


import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * The FundContract is a smart contract which resembles a set of rules that all participants must agree to
 * in order to verify a transaction and sign. The rules differ between Commands/transaction types.
 */
public class FundContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.newamerica.contracts.FundContract";

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements Commands {}
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<Commands> command = requireSingleCommand(tx.getCommands(), Commands.class);
        final Commands commandData = command.getValue();

        if(commandData.equals(new Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a FundState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a FundState.", tx.getOutputStates().size() == 1);
                FundState outputState = (FundState) tx.getOutputStates().get(0);
                require.using("OriginCountry and ReceivingCountry cannot be the same Party", outputState.originCountry == outputState.receivingCountry);
                require.using("There must be at least one Party in the owner list.", outputState.owners.isEmpty());
                require.using("There must be at least one Party in the requiredSigners list.", outputState.owners.isEmpty());
                require.using("The amount must be greater than zero.", outputState.amount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The balance must be greater than zero.", outputState.balance.compareTo(BigDecimal.ZERO) > 0);
                require.using("the maxWithdrawalAmount must be greater than or equal to zero", outputState.maxWithdrawalAmount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The status can only be ISSUED during an issuance transaction.", outputState.status == FundState.FundStateStatus.ISSUED);
                require.using("The set of participants cannot be empty.", outputState.participants.isEmpty());
                require.using("The isReceived flag must be FALSE during issuance.", !outputState.isReceived);

                // combine the sets
                Set<Party> combinedSets = outputState.owners;
                combinedSets.addAll(outputState.requiredSigners);
                require.using("All owners and requiredSigners must be in the participant set.", outputState.participants.containsAll(combinedSets));
                return null;
            });
        }
    }
}