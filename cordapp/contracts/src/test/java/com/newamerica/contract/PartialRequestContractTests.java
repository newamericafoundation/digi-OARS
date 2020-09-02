package com.newamerica.contract;

import com.newamerica.contracts.PartialRequestContract;
import com.newamerica.states.PartialRequestState;
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

public class PartialRequestContractTests {
    static private final MockServices ledgerServices =
            new MockServices(Arrays.asList("com.newamerica.contracts", "com.newamerica.flows"));
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> authorizedParties = new ArrayList<>();
    private PartialRequestState partialRequestState;

    @Before
    public void setup() {
        participants.add(CATANMoJ.getParty());
        participants.add(CATAN_CSO.getParty());
        participants.add(US_CSO.getParty());
        authorizedParties.add(CATANTreasury.getParty());

        //create partial request state
        partialRequestState = new PartialRequestState(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );

    }

    @Test
    public void txMustIncludeIssueCommand() {
        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.fails();
                tx.command(CATANMoJ.getPublicKey(), new PartialRequestContract.Commands.Issue());
                tx.verifies();
                return null;
            });
            return null;
        });
    }

    @Test(expected=AssertionError.class)
    public void txMustHaveNoInputs() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.input(PartialRequestContract.ID, partialRequestState);
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.command(CATANMoJ.getPublicKey(), new PartialRequestContract.Commands.Issue());
                tx.failsWith("No inputs should be consumed when issuing an Transfer.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void transactionMustHaveOneOutput() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.command(CATANMoJ.getPublicKey(), new PartialRequestContract.Commands.Issue());
                tx.failsWith("Only one output state should be created.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void catanMoJMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.command(CATANMoJ.getPublicKey(), new PartialRequestContract.Commands.Issue());
                tx.failsWith("Catan MoJ must be signer");
                return null;
            });
            return null;
        }));
    }


    @Test(expected=AssertionError.class)
    public void cannotCreateNegativeValueRequest() {
        PartialRequestState partialRequestState = new PartialRequestState(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000).negate(),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(PartialRequestContract.ID, partialRequestState);
                tx.command(CATANMoJ.getPublicKey(), new PartialRequestContract.Commands.Issue());
                tx.failsWith("The request value must be non-negative.");
                return null;
            });
            return null;
        }));
    }
}
