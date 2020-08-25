package com.newamerica.contracts;

import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class RequestContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.newamerica.contracts.RequestContract";

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements FundContract.Commands {}
    }
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<FundContract.Commands> command = requireSingleCommand(tx.getCommands(), FundContract.Commands.class);
        final FundContract.Commands commandData = command.getValue();

        if(commandData.equals(new Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a RequestState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a RequestState.", tx.getOutputStates().size() == 1);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                return null;
            });
        }
    }
}
