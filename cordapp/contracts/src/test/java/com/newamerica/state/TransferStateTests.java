package com.newamerica.state;

import com.newamerica.states.TransferState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static com.newamerica.TestUtils.CATANTreasury;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransferStateTests {
    private TransferState transferState;
    private final List<AbstractParty> participants = new ArrayList<>();
    private UniqueIdentifier uniqueIdentifier;

    @Before
    public void setup(){
        participants.add(CATANTreasury.getParty());
        uniqueIdentifier =  new UniqueIdentifier();

        transferState = new TransferState(
                CATANTreasury.getParty(),
                "Catan Ministry of Education",
                "Alice Bob",
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                uniqueIdentifier,
                participants
        );
    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field issuanceParty = TransferState.class.getDeclaredField("issuanceParty");
        Field receivingDept = TransferState.class.getDeclaredField("receivingDept");
        Field authorizedUserUsername = TransferState.class.getDeclaredField("authorizedUserUsername");
        Field externalAccountId = TransferState.class.getDeclaredField("externalAccountId");
        Field amount = TransferState.class.getDeclaredField("amount");
        Field datetime = TransferState.class.getDeclaredField("datetime");
        Field currency = TransferState.class.getDeclaredField("currency");
        Field requestStateLinearId = TransferState.class.getDeclaredField("requestStateLinearId");
        Field participants = TransferState.class.getDeclaredField("participants");

        assertTrue(authorizedUserUsername.getType().isAssignableFrom(String.class));
        assertTrue(issuanceParty.getType().isAssignableFrom(Party.class));
        assertTrue(receivingDept.getType().isAssignableFrom(String.class));
        assertTrue(externalAccountId.getType().isAssignableFrom(String.class));
        assertTrue(amount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(datetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(requestStateLinearId.getType().isAssignableFrom(UniqueIdentifier.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
    }


    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){
        assertEquals(transferState.getIssuanceParty(), CATANTreasury.getParty());
        assertEquals(transferState.getAuthorizedUserUsername(), "Alice Bob");
        assertEquals(transferState.getReceivingDept(), "Catan Ministry of Education");
        assertTrue(transferState.getAmount().compareTo(BigDecimal.valueOf(1000000)) == 0);
        assertEquals(transferState.getDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertEquals(transferState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(transferState.getExternalAccountId(), "1234567890");
        assertEquals(transferState.getRequestStateLinearId(), uniqueIdentifier);
        assertEquals(transferState.getParticipants(),new ArrayList<>(participants));
    }


}
