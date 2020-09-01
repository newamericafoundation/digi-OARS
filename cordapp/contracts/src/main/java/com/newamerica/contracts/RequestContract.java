package com.newamerica.contracts;

import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class RequestContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String ID = "com.newamerica.contracts.RequestContract";

    // Used to indicate the transaction's intent.
    public interface Commands extends CommandData {
        class Issue extends TypeOnlyCommandData implements RequestContract.Commands {}
        class Approve extends TypeOnlyCommandData implements RequestContract.Commands {}
    }
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        final CommandWithParties<RequestContract.Commands> command = requireSingleCommand(tx.getCommands(), RequestContract.Commands.class);
        final RequestContract.Commands commandData = command.getValue();

        if(commandData.equals(new Commands.Issue())){
            requireThat(require -> {
                require.using("No inputs should be consumed when issuing a RequestState.", tx.getInputStates().size() == 0);
                require.using("Only one output state should be created when issuing a RequestState.", tx.getOutputStates().size() == 1);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                require.using("RequestState status must be either PENDING or FLAGGED", outputState.status != RequestState.RequestStateStatus.APPROVED );
                return null;
            });
        }else if(commandData.equals(new Commands.Approve())){
            requireThat(require -> {
                require.using("Only one inputs should be consumed when approving a RequestState.", tx.getInputStates().size() == 1);
                require.using("Only one output state should be created when approving a RequestState.", tx.getOutputStates().size() == 1);
                RequestState inputState = (RequestState) tx.getInputStates().get(0);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                require.using("The request input state must be in PENDING status.", inputState.getStatus() == RequestState.RequestStateStatus.PENDING);
                require.using("The request output state must be in APPROVED status.", inputState.getStatus() == RequestState.RequestStateStatus.APPROVED);
                require.using("The authorizedUserUsername cannot change.", inputState.getAuthorizedUserUsername() == outputState.getAuthorizedUserUsername());
                require.using("The authorizedUserDept cannot change.", inputState.getAuthorizedUserDept() == outputState.getAuthorizedUserDept());
                require.using("The authorizerUserUsername cannot change.", inputState.getAuthorizerUserUsername() == outputState.getAuthorizerUserUsername());
                require.using("The authorizerDept cannot change.", inputState.getAuthorizerDept() == outputState.getAuthorizerDept());
                require.using("The externalAccountId cannot change.", inputState.getExternalAccountId() == outputState.getExternalAccountId());
                require.using("The amount cannot change.", inputState.getAmount() == outputState.getAmount());
                require.using("The currency cannot change.", inputState.getCurrency() == outputState.getCurrency());
                require.using("The datetime cannot change.", inputState.getDatetime() == outputState.getDatetime());
                require.using("The fundStateLinearId cannot change.", inputState.getFundStateLinearId() == outputState.getFundStateLinearId());
                require.using("The participants cannot change.", inputState.getParticipants() == outputState.getParticipants());

               return null;
            });
        }
    }
}
