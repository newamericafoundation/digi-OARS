package com.newamerica.contracts;

import com.newamerica.states.TransferState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class TransferContract implements Contract {
    public static final String ID = "com.newamerica.contracts.TransferContract";

    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements TransferContract.Commands {}
    }

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<TransferContract.Commands> command = requireSingleCommand(tx.getCommands(), TransferContract.Commands.class);
        final TransferContract.Commands commandData = command.getValue();

        if(commandData.equals(new Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a TransferState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a TransferState.", tx.getOutputStates().size() == 1);
                TransferState outputState = (TransferState) tx.getOutputStates().get(0);
                require.using("The amount must be greater than zero.", outputState.amount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The List of participants cannot be empty.", outputState.participants.isEmpty());
                return null;
            });
        }
    }

}
