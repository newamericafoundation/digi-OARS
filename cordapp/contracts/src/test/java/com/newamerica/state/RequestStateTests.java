package com.newamerica.state;

import com.newamerica.states.RequestState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.newamerica.TestUtils.CATANMoFA;
import static com.newamerica.TestUtils.CATANMoJ;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RequestStateTests {
    private RequestState requestState;
    private final List<AbstractParty> participants = new ArrayList<>();
    private UniqueIdentifier uniqueIdentifier;

    @Before
    public void setup(){
        participants.add(CATANMoJ.getParty());
        participants.add(CATANMoFA.getParty());
        uniqueIdentifier =  new UniqueIdentifier("", UUID.randomUUID());

        requestState = new RequestState(
                "Alice Bob",
                "Catan Ministry of Education",
                "Chris Jones",
                CATANMoJ.getParty(),
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                RequestState.RequestStateStatus.PENDING,
                uniqueIdentifier,
                participants
        );

    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field authorizedUserUsername = RequestState.class.getDeclaredField("authorizedUserUsername");
        Field authorizedUserDept = RequestState.class.getDeclaredField("authorizedUserDept");
        Field authorizerUserUsername = RequestState.class.getDeclaredField("authorizerUserUsername");
        Field authorizerDept = RequestState.class.getDeclaredField("authorizerDept");
        Field externalAccountId = RequestState.class.getDeclaredField("externalAccountId");
        Field amount = RequestState.class.getDeclaredField("amount");
        Field datetime = RequestState.class.getDeclaredField("datetime");
        Field currency = RequestState.class.getDeclaredField("currency");
        Field status = RequestState.class.getDeclaredField("status");
        Field fundStateLinearId = RequestState.class.getDeclaredField("fundStateLinearId");
        Field participants = RequestState.class.getDeclaredField("participants");

        assertTrue(authorizedUserUsername.getType().isAssignableFrom(String.class));
        assertTrue(authorizedUserDept.getType().isAssignableFrom(String.class));
        assertTrue(authorizerUserUsername.getType().isAssignableFrom(String.class));
        assertTrue(authorizerDept.getType().isAssignableFrom(Party.class));
        assertTrue(externalAccountId.getType().isAssignableFrom(String.class));
        assertTrue(amount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(datetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(fundStateLinearId.getType().isAssignableFrom(UniqueIdentifier.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(status.getType().isAssignableFrom(RequestState.RequestStateStatus.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
    }


    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){

        assertEquals(requestState.getAuthorizedUserUsername(), "Alice Bob");
        assertEquals(requestState.getAuthorizedUserDept(), "Catan Ministry of Education");
        assertEquals(requestState.getAuthorizerUserUsername(),"Chris Jones");
        assertEquals(requestState.getAuthorizerDept(), CATANMoJ.getParty());
        assertTrue(requestState.getAmount().compareTo(BigDecimal.valueOf(1000000)) == 0);
        assertEquals(requestState.getDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertEquals(requestState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(requestState.getStatus(), RequestState.RequestStateStatus.PENDING);
        assertEquals(requestState.getExternalAccountId(), "1234567890");
        assertEquals(requestState.getfundStateLinearId(), uniqueIdentifier);
        assertEquals(requestState.getParticipants(),new ArrayList<>(participants));
    }
}
