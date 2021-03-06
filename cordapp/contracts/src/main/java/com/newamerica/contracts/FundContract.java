package com.newamerica.contracts;

import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        class Withdraw extends TypeOnlyCommandData implements Commands {}
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
                require.using("originParty and receivingParty cannot be the same Party.", outputState.originParty != outputState.receivingParty);
                require.using("There must be at least one Party in the owner list.", !outputState.owners.isEmpty());
                require.using("There must be at least one Party in the requiredSigners list.", !outputState.authorizedParties.isEmpty());
                require.using("The amount must be greater than zero.", outputState.amount.compareTo(BigDecimal.ZERO) > 0);
                require.using("The balance must be greater than zero.", outputState.balance.compareTo(BigDecimal.ZERO) > 0);
                require.using("The balance and amount fields must be equal during an issuance.", outputState.getAmount().compareTo(outputState.getBalance()) == 0);
                require.using("The status can only be ISSUED during an issuance transaction.", outputState.getStatus() == FundState.FundStateStatus.ISSUED);
                require.using("The create datetime and update datetime must be the same when issue.", outputState.getCreateDatetime().equals(outputState.getUpdateDatetime()));
                // combine the Lists
                List<AbstractParty> combinedLists = Stream.concat(outputState.owners.stream(), outputState.authorizedParties.stream()) .collect(Collectors.toList());
                require.using("All owners and requiredSigners must be in the participant List.", outputState.getParticipants().containsAll(combinedLists));
                return null;
            });
        }else if(commandData.equals(new Commands.Withdraw())){
            requireThat(require -> {
                require.using("One input should be consumed when updating the balance of a FundState.", tx.getInputStates().size() == 1);
                require.using("Only one output state should be created when updating the balance of a FundState.", tx.getOutputStates().size() == 1);

                FundState inputState = (FundState) tx.getInputStates().get(0);
                require.using("The inputFundState's status must be RECEIVED in order to proceed.", inputState.getStatus() == FundState.FundStateStatus.RECEIVED);

                FundState outputState = (FundState) tx.getOutputStates().get(0);

                require.using("If balance is zero, then the status should be PAID OR If balance is greater than zero, then the status should be RECEIVED",
                        (outputState.getBalance().compareTo(BigDecimal.ZERO) == 0 && outputState.getStatus() == FundState.FundStateStatus.PAID)
                                || (outputState.getBalance().compareTo(BigDecimal.ZERO) > 0 && outputState.getStatus() == FundState.FundStateStatus.RECEIVED));
                require.using("The withdrawal cannot result in a negative balance.", outputState.getBalance().compareTo(BigDecimal.ZERO) >= 0);
                require.using("The originParty cannot change.", inputState.getOriginParty().equals(outputState.getOriginParty()));
                require.using("The receivingParty cannot change.", inputState.getReceivingParty().equals(outputState.getReceivingParty()));
                require.using("The owners cannot change.", inputState.getOwners().equals(outputState.getOwners()));
                require.using("The requiredSigners cannot change.", inputState.getAuthorizedParties().equals(outputState.getAuthorizedParties()));
                require.using("The amount cannot change.", inputState.getAmount().equals(outputState.getAmount()));
                require.using("The currency cannot change.", inputState.getCurrency().equals(outputState.getCurrency()));
                require.using("The participants cannot change.", inputState.getParticipants().equals(outputState.getParticipants()));
                require.using("update datetime must be later than create datetime.", outputState.getUpdateDatetime().isAfter(outputState.getCreateDatetime()));
                return null;
            });
        }else if(commandData.equals(new Commands.Receive())){
            requireThat(require -> {
                require.using("1 input should be consumed when receiving a FundState.", tx.getInputStates().size() == 1);
                require.using("Only 1 output state should be created when receiving a FundState.", tx.getOutputStates().size() == 1);
                FundState inputState = (FundState) tx.getInputStates().get(0);
                require.using("The inputFundState's status must be ISSUED in order to proceed.", inputState.getStatus() == FundState.FundStateStatus.ISSUED);
                FundState outputState = (FundState) tx.getOutputStates().get(0);
                require.using("OriginParty and receivingParty cannot be the same Party", outputState.getOriginParty() != outputState.getReceivingParty());
                require.using("Received by username cannot be empty.", outputState.getReceivedByUsername() != null);
                require.using("There must be at least one Party in the owner list.", !outputState.getOwners().isEmpty());
                require.using("There must be at least one Party in the requiredSigners list.", !outputState.getOwners().isEmpty());
                require.using("The amount must be greater than or equal to zero.", outputState.getAmount().compareTo(BigDecimal.ZERO) >= 0);
                require.using("The balance must be greater than or equal to zero.", outputState.getBalance().compareTo(BigDecimal.ZERO) >= 0);
                require.using("The status can only be RECEIVED during an issuance transaction.", outputState.getStatus() == FundState.FundStateStatus.RECEIVED);
                require.using("The List of participants cannot be empty.", !outputState.getParticipants().isEmpty());
                require.using("input amount and output amount must be the same", inputState.getAmount().compareTo(outputState.getAmount()) == 0);
                require.using("input balance and output balance must be the same", inputState.getBalance().compareTo(outputState.getBalance()) == 0);
                require.using("input originParty and output originParty must be the same", inputState.getOriginParty().equals(outputState.getOriginParty()));
                require.using("input receivingParty and output receivingParty must be the same", inputState.getReceivingParty().equals(outputState.getReceivingParty()));
                // combine the Lists
                List<AbstractParty> combinedLists = Stream.of(outputState.getOwners(), outputState.getAuthorizedParties()).flatMap(Collection::stream).collect(Collectors.toList());
                require.using("All owners and requiredSigners must be in the participant List.", outputState.getParticipants().containsAll(combinedLists));
                require.using("update datetime must be later than create datetime.", outputState.getUpdateDatetime().isAfter(outputState.getCreateDatetime()));
                return null;
            });
        }
    }
}