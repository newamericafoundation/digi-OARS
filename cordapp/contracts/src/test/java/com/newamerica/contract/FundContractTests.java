package com.newamerica.contract;

import com.newamerica.contracts.FundContract;
import com.newamerica.states.FundState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.identity.AbstractParty;
import net.corda.testing.node.MockServices;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.newamerica.TestUtils.*;
import static net.corda.testing.node.NodeTestUtils.ledger;

public class FundContractTests {
    // A pre-defined dummy command.
    public interface Commands extends CommandData {
        class DummyCommand extends TypeOnlyCommandData implements Commands{}
    }

    static private final MockServices ledgerServices = new MockServices(
            Arrays.asList("com.newamerica", "com.newamerica.contracts")
    );

    private final List<AbstractParty> owners = new ArrayList<>();
    private final List<AbstractParty> requiredSigners = new ArrayList<>();
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> partialRequestParticipants = new ArrayList<>();


    @Before
    public void setup() {
        owners.add(US.getParty());
        requiredSigners.add(US.getParty());
        requiredSigners.add(CATAN.getParty());
        participants.add(US.getParty());
        participants.add(CATAN.getParty());
        partialRequestParticipants.add(US_CSO.getParty());
        partialRequestParticipants.add(CATAN_CSO.getParty());
    }

    // Commands not included in the FundContract should not be permitted.
    @Test
    public void mustIncludeValidCommand() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.output(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new Commands.DummyCommand());
                return tx.failsWith("Contract verification failed");
            });
            l.transaction(tx -> {
                tx.output(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }

    // The issuance command should fail if an inpute state is provided.
    @Test
    public void issueTransactionMustHaveNoInputs(){
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.input(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs.withdraw(BigDecimal.valueOf(200)));
                return tx.failsWith("No inputs should be consumed when issuing a FundState.");
            });
            l.transaction(tx -> {
                tx.output(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }

    // An issuance must produce an output state.
    @Test
    public void issueTransactionMustHaveOneOutput() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                tx.output(FundContract.ID, fs.withdraw(BigDecimal.valueOf(200)));
                return tx.failsWith("Only one output state should be created when issuing a FundState.");
            });
            l.transaction(tx -> {
                tx.output(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }

    // An issuance is not valid if the origin and receiving countries are the same.
    @Test
    public void originAndReceivingPartyCannotBeTheSame() {
        FundState fs = new FundState(
                US.getParty(),
                US.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("originParty and receivingParty cannot be the same Party.");
            });
            return null;
        });
    }

    // An issuance is not valid if the owners list is empty.
    @Test
    public void ownerListCannotBeEmpty() {
        owners.clear();
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("There must be at least one Party in the owner list.");
            });
            return null;
        });
    }

    // An issuance is not valid if the requiredSigners list is empty.
    @Test
    public void requiredSignersListCannotBeEmpty() {
        requiredSigners.clear();
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("There must be at least one Party in the requiredSigners list.");
            });
            return null;
        });
    }

    // An issuance is not valid if the amount field is ZERO or less
    @Test
    public void amountGreaterThanZero() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(0),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("The amount must be greater than zero.");
            });
            return null;
        });
    }

    // An issuance is not valid if the balance field is ZERO or less
    @Test
    public void balanceGreaterThanZero() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(0),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("The balance must be greater than zero.");
            });
            return null;
        });
    }

    // An issuance is not valid if the amount and balance fields have non-equal values.
    @Test
    public void amountAndBalanceMustBeEqual() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000001),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("The balance and amount fields must be equal during an issuance.");
            });
            return null;
        });
    }

    // An issuance is not valid if the maxWithdrawalLimit is not greater than or equal to zero.
    @Test
    public void maxWithdrawalLimitPositiveValue() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(-1),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("The maxWithdrawalAmount must be greater than or equal to zero.");
            });
            return null;
        });
    }

    // An issuance is not valid if the status is not ISSUED.
    @Test
    public void issuedStatus() {
        FundState fs = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.PAID,
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((fs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new FundContract.Commands.Issue());
                tx.output(FundContract.ID, fs);
                return tx.failsWith("The status can only be ISSUED during an issuance transaction.");
            });
            return null;
        });
    }
}
