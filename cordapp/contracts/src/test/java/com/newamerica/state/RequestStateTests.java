package com.newamerica.state;

import com.google.common.collect.ImmutableMap;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.apache.shiro.crypto.hash.Hash;
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
    private final List<AbstractParty> authorizedParties = new ArrayList<>();
    private UniqueIdentifier uniqueIdentifier;
    private Map<String, String> authorizerUserPartyAndUsername = new LinkedHashMap<>();

    @Before
    public void setup(){
        participants.add(CATANMoJ.getParty());
        participants.add(CATANMoFA.getParty());
        uniqueIdentifier =  new UniqueIdentifier();
        authorizedParties.add(CATANMoJ.getParty());
        authorizedParties.add(CATANMoFA.getParty());
        authorizerUserPartyAndUsername.put("Catan MOJ", "Chris Jones");

        requestState = new RequestState(
                "Tom Tom",
                "some reason",
                BigDecimal.valueOf(1000000),
                "Alice Bob",
                "Catan Ministry of Education",
                authorizerUserPartyAndUsername,
                authorizedParties,
                "1234567890",
                "build a school",
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                RequestState.RequestStateStatus.PENDING,
                uniqueIdentifier,
                participants
        );

    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field transferUsername = RequestState.class.getDeclaredField("transferUsername");
        Field rejectReason = RequestState.class.getDeclaredField("rejectReason");
        Field maxWithdrawalAmount = RequestState.class.getDeclaredField("maxWithdrawalAmount");
        Field authorizedUserUsername = RequestState.class.getDeclaredField("authorizedUserUsername");
        Field authorizedUserDept = RequestState.class.getDeclaredField("authorizedUserDept");
        Field authorizerUserDeptAndUsername = RequestState.class.getDeclaredField("authorizerUserDeptAndUsername");
        Field authorizedParties = RequestState.class.getDeclaredField("authorizedParties");
        Field externalAccountId = RequestState.class.getDeclaredField("externalAccountId");
        Field purpose = RequestState.class.getDeclaredField("purpose");
        Field amount = RequestState.class.getDeclaredField("amount");
        Field createDatetime = FundState.class.getDeclaredField("createDatetime");
        Field updateDatetime = FundState.class.getDeclaredField("updateDatetime");
        Field currency = RequestState.class.getDeclaredField("currency");
        Field status = RequestState.class.getDeclaredField("status");
        Field fundStateLinearId = RequestState.class.getDeclaredField("fundStateLinearId");
        Field participants = RequestState.class.getDeclaredField("participants");

        assertTrue(authorizedUserUsername.getType().isAssignableFrom(String.class));
        assertTrue(authorizedUserDept.getType().isAssignableFrom(String.class));
        assertTrue(authorizerUserDeptAndUsername.getType().isAssignableFrom(Map.class));
        assertTrue(authorizedParties.getType().isAssignableFrom(List.class));
        assertTrue(externalAccountId.getType().isAssignableFrom(String.class));
        assertTrue(purpose.getType().isAssignableFrom(String.class));
        assertTrue(amount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(createDatetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(updateDatetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(fundStateLinearId.getType().isAssignableFrom(UniqueIdentifier.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(status.getType().isAssignableFrom(RequestState.RequestStateStatus.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
        assertTrue(maxWithdrawalAmount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(rejectReason.getType().isAssignableFrom(String.class));
        assertTrue(transferUsername.getType().isAssignableFrom(String.class));
    }


    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){

        assertEquals(requestState.getAuthorizedUserUsername(), "Alice Bob");
        assertEquals(requestState.getAuthorizedUserDept(), "Catan Ministry of Education");
        assertTrue(requestState.getAuthorizerUserDeptAndUsername().containsValue("Chris Jones"));
        assertEquals(requestState.getAuthorizedParties(), new ArrayList<>(authorizedParties));
        assertEquals(0, requestState.getAmount().compareTo(BigDecimal.valueOf(1000000)));
        assertEquals(requestState.getCreateDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertEquals(requestState.getUpdateDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertEquals(requestState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(requestState.getStatus(), RequestState.RequestStateStatus.PENDING);
        assertEquals(requestState.getExternalAccountId(), "1234567890");
        assertEquals(requestState.getPurpose(), "build a school");
        assertEquals(requestState.getFundStateLinearId(), uniqueIdentifier);
        assertEquals(requestState.getParticipants(),new ArrayList<>(participants));
        assertEquals(0, requestState.getMaxWithdrawalAmount().compareTo(BigDecimal.valueOf(1000000)));
        assertEquals(requestState.getRejectReason(), "some reason");
        assertEquals(requestState.getTransferUsername(), "Tom Tom");



    }
}
