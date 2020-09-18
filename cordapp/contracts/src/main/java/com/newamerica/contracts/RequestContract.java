package com.newamerica.contracts;

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
        class Issue extends TypeOnlyCommandData implements RequestContract.Commands {}
        class Approve extends TypeOnlyCommandData implements RequestContract.Commands {}
        class Reject extends TypeOnlyCommandData implements RequestContract.Commands {}
        class Transfer extends TypeOnlyCommandData implements RequestContract.Commands {}
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
                require.using("AuthorizedParties list cannot be empty.", !outputState.getAuthorizedParties().isEmpty());
                require.using("The create datetime and update datetime must be the same when issue.", outputState.getCreateDatetime().equals(outputState.getUpdateDatetime()));
                return null;
            });
        }else if(commandData.equals(new Commands.Approve())){
            requireThat(require -> {
                require.using("Only one input state should be consumed when approving a RequestState.", tx.getInputStates().size() == 1);
                require.using("Only one output state should be created when approving a RequestState.", tx.getOutputStates().size() == 1);
                RequestState inputState = (RequestState) tx.getInputStates().get(0);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                require.using("The request input state must be in PENDING status.", inputState.getStatus() == RequestState.RequestStateStatus.PENDING);
                require.using("The request output state must be in APPROVED status.", outputState.getStatus() == RequestState.RequestStateStatus.APPROVED);
                require.using("The authorizedUserUsername cannot change.", inputState.getAuthorizedUserUsername().equals(outputState.getAuthorizedUserUsername()));
                require.using("The authorizedUserDept cannot change.", inputState.getAuthorizedUserDept().equals(outputState.getAuthorizedUserDept()));
                require.using("The authorizerUserDeptAndUsername cannot be null.", !outputState.getAuthorizerUserDeptAndUsername().isEmpty());
                require.using("The authorizerDept cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The externalAccountId cannot change.", inputState.getExternalAccountId().equals(outputState.getExternalAccountId()));
                require.using("The purpose cannot change.", inputState.getPurpose().equals(outputState.getPurpose()));
                require.using("The amount cannot change.", inputState.getAmount().equals(outputState.getAmount()));
                require.using("The currency cannot change.", inputState.getCurrency().equals(outputState.getCurrency()));
                require.using("The fundStateLinearId cannot change.", inputState.getFundStateLinearId().equals(outputState.getFundStateLinearId()));
                require.using("The authorizedParties cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The participants cannot change.", inputState.getParticipants().equals(outputState.getParticipants()));
                require.using("update datetime must be later than create datetime.", outputState.getUpdateDatetime().isAfter(outputState.getCreateDatetime()));
                return null;
            });
        }else if(commandData.equals(new Commands.Reject())){
            requireThat(require -> {
                require.using("Only one input state should be consumed when rejecting a RequestState.", tx.getInputStates().size() == 1);
                require.using("Only one output state should be created when rejecting a RequestState.", tx.getOutputStates().size() == 1);
                RequestState inputState = (RequestState) tx.getInputStates().get(0);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                require.using("The request input state must be in PENDING status.", inputState.getStatus() == RequestState.RequestStateStatus.PENDING);
                require.using("The request output state must be in REJECTED status.", outputState.getStatus() == RequestState.RequestStateStatus.APPROVED);
                require.using("The authorizedUserUsername cannot change.", inputState.getAuthorizedUserUsername().equals(outputState.getAuthorizedUserUsername()));
                require.using("The authorizedUserDept cannot change.", inputState.getAuthorizedUserDept().equals(outputState.getAuthorizedUserDept()));
                require.using("The authorizerUserDeptAndUsername cannot be null.", !outputState.getAuthorizerUserDeptAndUsername().isEmpty());
                require.using("The authorizerDept cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The externalAccountId cannot change.", inputState.getExternalAccountId().equals(outputState.getExternalAccountId()));
                require.using("The purpose cannot change.", inputState.getPurpose().equals(outputState.getPurpose()));
                require.using("The amount cannot change.", inputState.getAmount().equals(outputState.getAmount()));
                require.using("The currency cannot change.", inputState.getCurrency().equals(outputState.getCurrency()));
                require.using("The fundStateLinearId cannot change.", inputState.getFundStateLinearId().equals(outputState.getFundStateLinearId()));
                require.using("The authorizedParties cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The participants cannot change.", inputState.getParticipants().equals(outputState.getParticipants()));
                require.using("update datetime must be later than create datetime.", outputState.getUpdateDatetime().isAfter(outputState.getCreateDatetime()));
                return null;
            });
        }else if(commandData.equals(new Commands.Transfer())){
            requireThat(require -> {
                require.using("Only one input state should be consumed when a transfer happens on a RequestState.", tx.getInputStates().size() == 1);
                require.using("Only one output state should be created when a transfer happens on a RequestState.", tx.getOutputStates().size() == 1);
                RequestState inputState = (RequestState) tx.getInputStates().get(0);
                RequestState outputState = (RequestState) tx.getOutputStates().get(0);
                require.using("The request input state must be in APPROVED status.", inputState.getStatus() == RequestState.RequestStateStatus.APPROVED);
                require.using("The request output state must be in TRANSFERRED status.", outputState.getStatus() == RequestState.RequestStateStatus.TRANSFERRED);
                require.using("The authorizedUserUsername cannot change.", inputState.getAuthorizedUserUsername().equals(outputState.getAuthorizedUserUsername()));
                require.using("The authorizedUserDept cannot change.", inputState.getAuthorizedUserDept().equals(outputState.getAuthorizedUserDept()));
                require.using("The authorizerUserDeptAndUsername cannot be null.", !outputState.getAuthorizerUserDeptAndUsername().isEmpty());
                require.using("The authorizerDept cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The externalAccountId cannot change.", inputState.getExternalAccountId().equals(outputState.getExternalAccountId()));
                require.using("The purpose cannot change.", inputState.getPurpose().equals(outputState.getPurpose()));
                require.using("The amount cannot change.", inputState.getAmount().equals(outputState.getAmount()));
                require.using("The currency cannot change.", inputState.getCurrency().equals(outputState.getCurrency()));
                require.using("The fundStateLinearId cannot change.", inputState.getFundStateLinearId().equals(outputState.getFundStateLinearId()));
                require.using("The authorizedParties cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The participants cannot change.", inputState.getParticipants().equals(outputState.getParticipants()));
                require.using("update datetime must be later than create datetime.", outputState.getUpdateDatetime().isAfter(outputState.getCreateDatetime()));
                return null;
            });
        }
    }
}
