package com.newamerica.contract;

import com.newamerica.contracts.RequestContract;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.testing.node.MockServices;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static com.newamerica.TestUtils.*;
import static net.corda.testing.node.NodeTestUtils.ledger;

public class RequestContractTests {
    static private final MockServices ledgerServices =
            new MockServices(Arrays.asList("com.newamerica.contracts", "com.newamerica.flows"));
    private final List<AbstractParty> participants = new ArrayList<>();
    private RequestState requestState;
    private RequestState requestState_diff;
    private RequestState requestState_nagative_amount;

    public interface Commands extends CommandData {
        class DummyCommand extends TypeOnlyCommandData implements Commands{}
    }

    @Before
    public void setup() {
        participants.add(US_DoJ.getParty());
        participants.add(US_DoS.getParty());
        participants.add(NewAmerica.getParty());
        participants.add(CATANTreasury.getParty());
        participants.add(CATANMoFA.getParty());
        participants.add(CATANMoJ.getParty());

        //create transfer state
        requestState = new RequestState(
                "Alice Bob",
                "Catan Ministry of Education",
                "Chris Blue",
                CATANMoJ.getParty(),
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                RequestState.RequestStateStatus.PENDING,
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );

        //create transfer state
        requestState_diff = new RequestState(
                "Alice Alice",
                "Catan Ministry of Education",
                "Chris Blue",
                CATANMoJ.getParty(),
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                RequestState.RequestStateStatus.APPROVED,
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );

        requestState_nagative_amount = new RequestState(
                "Alice Bob",
                "Catan Ministry of Education",
                "Chris Blue",
                CATANMoJ.getParty(),
                "1234567890",
                BigDecimal.valueOf(1000000).negate(),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                RequestState.RequestStateStatus.PENDING,
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );
    }

    // issue
    @Test(expected= TransactionVerificationException.class)
    public void mustHandleMultipleCommandValues() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.output(RequestContract.ID, requestState);
                tx.command(CATANMoFA.getPublicKey(), new Commands.DummyCommand());
                return tx.failsWith("Required com.newamerica.contracts.RequestContract.Commands command");
            });
            l.transaction(tx -> {
                tx.output(RequestContract.ID, requestState);
                tx.fails();
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Issue());
                return tx.verifies();
            });
            l.transaction(tx -> {
                tx.input(RequestContract.ID, requestState);
                tx.output(RequestContract.ID, requestState.changeStatus(RequestState.RequestStateStatus.APPROVED));
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Approve());
                return tx.verifies();
            });
            return null;
        });
    }

    @Test(expected=AssertionError.class)
    public void txMustHaveNoInputs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(RequestContract.ID, requestState);
                tx.output(RequestContract.ID, requestState);
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Issue());
                tx.failsWith("No inputs should be consumed when issuing a request.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void txMustHaveOneOutput() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(RequestContract.ID, requestState);
                tx.output(RequestContract.ID, requestState);
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Issue());
                tx.failsWith("Only one output state should be created when issue.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void catanMoFAMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(RequestContract.ID, requestState);
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Issue());
                tx.failsWith("Catan MoFA must be signer on issue");
                return null;
            });
            return null;
        }));
    }


    @Test(expected=AssertionError.class)
    public void cannotCreateNegativeValueRequest() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(RequestContract.ID, requestState_nagative_amount);
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Issue());
                tx.failsWith("The request value must be non-negative.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void onlyTheStatusOfRequestCanChangeAfterApprove() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(RequestContract.ID, requestState);
                tx.output(RequestContract.ID, requestState_diff);
                tx.command(CATANMoFA.getPublicKey(), new RequestContract.Commands.Approve());
                tx.failsWith("only status of the output can change");
                return null;
            });
            return null;
        }));
    }
}
