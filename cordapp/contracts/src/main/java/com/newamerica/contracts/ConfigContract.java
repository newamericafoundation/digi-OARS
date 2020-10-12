package com.newamerica.contracts;

import com.newamerica.states.ConfigState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ConfigContract implements Contract {
    public static final String ID = "com.newamerica.contracts.ConfigContract";

    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements ConfigContract.Commands {}
    }

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<ConfigContract.Commands> command = requireSingleCommand(tx.getCommands(), ConfigContract.Commands.class);
        final ConfigContract.Commands commandData = command.getValue();
        if(commandData.equals(new ConfigContract.Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a ConfigState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a ConfigState.", tx.getOutputStates().size() == 1);
                ConfigState outputState = (ConfigState) tx.getOutputStates().get(0);
                require.using("The maxWithdrawalAmount must be greater than or equal to zero.", outputState.getMaxWithdrawalAmount().compareTo(BigDecimal.ZERO) >= 0);
                return null;
            });
        }
    }
}
