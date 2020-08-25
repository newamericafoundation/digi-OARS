package com.newamerica.contracts;

import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.math.BigDecimal;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * The FundContract is a smart contract which resembles a List of rules that all participants must agree to
 * in order to verify a transaction and sign. The rules differ between Commands/transaction types.
 */
public class FundContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.newamerica.contracts.FundContract";

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements Commands {}
        class Receive extends TypeOnlyCommandData implements Commands {}
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
                require.using("The List of participants cannot be empty.", outputState.participants.isEmpty());

                // combine the Lists
                List<Party> combinedLists = outputState.owners;
                combinedLists.addAll(outputState.requiredSigners);
                require.using("All owners and requiredSigners must be in the participant List.", outputState.participants.containsAll(combinedLists));
                return null;
            });
        }

        if(commandData.equals(new Commands.Receive())){
            requireThat(require -> {
                require.using("1 input should be consumed when receiving a FundState.", tx.getInputStates().size() == 1);
                require.using("Only 1 output state should be created when receiving a FundState.", tx.getOutputStates().size() == 1);
                FundState outputState = (FundState) tx.getOutputStates().get(0);
                require.using("OriginCountry and ReceivingCountry cannot be the same Party", outputState.originCountry == outputState.receivingCountry);
                require.using("There must be at least one Party in the owner list.", outputState.owners.isEmpty());
                require.using("There must be at least one Party in the requiredSigners list.", outputState.owners.isEmpty());
                require.using("The amount must be greater than zero.", outputState.amount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The balance must be greater than zero.", outputState.balance.compareTo(BigDecimal.ZERO) > 0);
                require.using("the maxWithdrawalAmount must be greater than or equal to zero", outputState.maxWithdrawalAmount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The status can only be RECEIVED during an issuance transaction.", outputState.status == FundState.FundStateStatus.RECEIVED);
                require.using("The List of participants cannot be empty.", outputState.participants.isEmpty());
                FundState inputState = (FundState) tx.getInputStates().get(0);
                require.using("input OriginCountry and output OriginCountry must be the same", inputState.originCountry == outputState.originCountry);
                require.using("input ReceivingCountry and output ReceivingCountry must be the same", inputState.receivingCountry == outputState.receivingCountry);
                require.using("input amount and output amount must be the same", inputState.amount == outputState.amount);
                require.using("input balance and output balance must be the same", inputState.balance == outputState.balance);
                require.using("input  max withdrawal amount and output max withdrawal amount must be the same", inputState.maxWithdrawalAmount == outputState.maxWithdrawalAmount);

                // combine the Lists
                List<Party> combinedLists = outputState.owners;
                combinedLists.addAll(outputState.requiredSigners);
                require.using("All owners and requiredSigners must be in the participant List.", outputState.participants.containsAll(combinedLists));
                return null;
            });
        }
    }
}