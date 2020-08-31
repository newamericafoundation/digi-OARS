package com.newamerica.contracts;

import com.newamerica.states.PartialRequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class PartialRequestContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.newamerica.contracts.PartialRequestContract";

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements PartialRequestContract.Commands {}
    }
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<PartialRequestContract.Commands> command = requireSingleCommand(tx.getCommands(), PartialRequestContract.Commands.class);
        final PartialRequestContract.Commands commandData = command.getValue();

        if(commandData.equals(new PartialRequestContract.Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a PartialRequestState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a PartialRequestState.", tx.getOutputStates().size() == 1);
                PartialRequestState outputState = (PartialRequestState) tx.getOutputStates().get(0);
                require.using("The amount must be greater than or equal to zero.", outputState.getAmount().compareTo(BigDecimal.ZERO) >= 0);
                return null;
            });
        }
    }
}
