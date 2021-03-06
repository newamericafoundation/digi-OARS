package com.newamerica.state;

import com.newamerica.states.PartialRequestState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
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

import static com.newamerica.TestUtils.CATANMoFA;
import static com.newamerica.TestUtils.CATANMoJ;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PartialRequestStateTests {

    private PartialRequestState partialrequestState;
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> authorizedParties = new ArrayList<>();
    private UniqueIdentifier uniqueIdentifier;

    @Before
    public void setup(){
        participants.add(CATANMoJ.getParty());
        participants.add(CATANMoFA.getParty());
        authorizedParties.add(CATANMoJ.getParty());
        uniqueIdentifier =  new UniqueIdentifier();

        partialrequestState = new PartialRequestState(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                "build a school",
                uniqueIdentifier,
                participants
        );

    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field authorizedUserDept = PartialRequestState.class.getDeclaredField("authorizedUserDept");
        Field authorizedParties = PartialRequestState.class.getDeclaredField("authorizedParties");
        Field amount = PartialRequestState.class.getDeclaredField("amount");
        Field datetime = PartialRequestState.class.getDeclaredField("datetime");
        Field currency = PartialRequestState.class.getDeclaredField("currency");
        Field purpose = PartialRequestState.class.getDeclaredField("purpose");
        Field fundStateLinearId = PartialRequestState.class.getDeclaredField("fundStateLinearId");
        Field participants = PartialRequestState.class.getDeclaredField("participants");

        assertTrue(authorizedUserDept.getType().isAssignableFrom(String.class));
        assertTrue(authorizedParties.getType().isAssignableFrom(List.class));
        assertTrue(amount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(datetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(fundStateLinearId.getType().isAssignableFrom(UniqueIdentifier.class));
        assertTrue(purpose.getType().isAssignableFrom(String.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
    }


    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){

        assertEquals(partialrequestState.getAuthorizedUserDept(), "Catan Ministry of Education");
        assertEquals(partialrequestState.getAuthorizedParties(), authorizedParties);
        assertTrue(partialrequestState.getAmount().compareTo(BigDecimal.valueOf(1000000)) == 0);
        assertEquals(partialrequestState.getDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertEquals(partialrequestState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(partialrequestState.getPurpose(), "build a school");
        assertEquals(partialrequestState.getFundStateLinearId(), uniqueIdentifier);
        assertEquals(partialrequestState.getParticipants(),new ArrayList<>(participants));
    }
}
