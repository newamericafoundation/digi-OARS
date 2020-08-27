package com.newamerica.contract;

import com.newamerica.contracts.TransferContract;
import com.newamerica.states.TransferState;
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



public class TransferContractTests {
    static private final MockServices ledgerServices =
            new MockServices(Arrays.asList("com.newamerica.contracts", "com.newamerica.flows"));
    private final List<AbstractParty> participants = new ArrayList<>();
    private TransferState transferState;
    private TransferState transferState_nagative_amount;


    @Before
    public void setup() {
        participants.add(US_DoJ.getParty());
        participants.add(US_DoS.getParty());
        participants.add(NewAmerica.getParty());
        participants.add(CATANTreasury.getParty());
        participants.add(CATANMoFA.getParty());
        participants.add(CATANMoJ.getParty());

        //create transfer state
        transferState = new TransferState(
                CATANTreasury.getParty(),
                "Catan Ministry of Education",
                "Alice Bob",
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                new UniqueIdentifier(),
                participants
        );

        transferState_nagative_amount = new TransferState(
                CATANTreasury.getParty(),
                "Catan Ministry of Education",
                "Alice Bob",
                "1234567890",
                BigDecimal.valueOf(1000000).negate(),
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
                tx.output(TransferContract.ID, transferState);
                tx.fails();
                tx.command(CATANTreasury.getPublicKey(), new TransferContract.Commands.Issue());
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
                tx.input(TransferContract.ID, transferState);
                tx.output(TransferContract.ID, transferState);
                tx.command(CATANTreasury.getPublicKey(), new TransferContract.Commands.Issue());
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
                tx.output(TransferContract.ID, transferState);
                tx.output(TransferContract.ID, transferState);
                tx.command(CATANTreasury.getPublicKey(), new TransferContract.Commands.Issue());
                tx.failsWith("Only one output state should be created.");
                return null;
            });
            return null;
        }));
    }

    @Test(expected=AssertionError.class)
    public void catanTreasuryMustSignTransaction() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(TransferContract.ID, transferState);
                tx.command(CATANTreasury.getPublicKey(), new TransferContract.Commands.Issue());
                tx.failsWith("Catan Treasury must be signer");
                return null;
            });
            return null;
        }));
    }


    @Test(expected=AssertionError.class)
    public void cannotCreateNegativeValueTransfers() {
        ledger(ledgerServices, (ledger -> {
            ledger.transaction(tx -> {
                tx.output(TransferContract.ID, transferState_nagative_amount);
                tx.command(CATANTreasury.getPublicKey(), new TransferContract.Commands.Issue());
                tx.failsWith("The Transfer value must be non-negative.");
                return null;
            });
            return null;
        }));
    }
}
