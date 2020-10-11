package com.newamerica.contract;

import com.newamerica.contracts.ConfigContract;
import com.newamerica.states.ConfigState;
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

public class ConfigContractTests {

    public interface Commands extends CommandData {
        class DummyCommand extends TypeOnlyCommandData implements ConfigContractTests.Commands {}
    }

    static private final MockServices ledgerServices = new MockServices(
            Arrays.asList("com.newamerica", "com.newamerica.contracts")
    );

    private final List<AbstractParty> participants = new ArrayList<>();


    @Before
    public void setup() {
        participants.add(US_DoJ.getParty());
        participants.add(CATANMoFA.getParty());
    }


    // An issuance is not valid if the maxWithdrawalLimit is not greater than or equal to zero.
    @Test
    public void maxWithdrawalLimitPositiveValue() {
        ConfigState cs = new ConfigState(
                "US DoJ",
                "Catan",
                BigDecimal.valueOf(-1),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                participants
        );

        ledger(ledgerServices, l -> {
            l.transaction(tx -> {
                tx.command((cs.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())), new ConfigContract.Commands.Issue());
                tx.output(ConfigContract.ID, cs);
                return tx.failsWith("The maxWithdrawalAmount must be greater than or equal to zero.");
            });
            return null;
        });
    }

}
