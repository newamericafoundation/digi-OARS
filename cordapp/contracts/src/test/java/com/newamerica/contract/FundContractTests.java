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

    private FundState fundState;
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
                tx.command((fs.getParticipants().stream().map(i -> i.getOwningKey()).collect(Collectors.toList())), new Commands.DummyCommand());
                return tx.failsWith("Contract verification failed");
            });
            l.transaction(tx -> {
                tx.output(FundContract.ID, fs);
                tx.command((fs.getParticipants().stream().map(i -> i.getOwningKey()).collect(Collectors.toList())), new FundContract.Commands.Issue());
                return tx.verifies();
            });
            return null;
        });
    }
}
