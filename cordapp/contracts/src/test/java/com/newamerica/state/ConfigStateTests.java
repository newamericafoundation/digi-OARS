package com.newamerica.state;

import com.newamerica.states.ConfigState;
import com.newamerica.states.FundState;
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

import static com.newamerica.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigStateTests {
    private ConfigState configState;
    private final List<AbstractParty> participants = new ArrayList<>();

    @Before
    public void setup(){
        participants.add(US_DoJ.getParty());
        participants.add(CATANMoFA.getParty());

        configState = new ConfigState(
                "US DoJ",
                "Catan",
                BigDecimal.valueOf(5000000),
                Currency.getInstance("USD"),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                participants
                );
    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{

        Field creator = ConfigState.class.getDeclaredField("creator");
        Field country = ConfigState.class.getDeclaredField("country");
        Field createDatetime = ConfigState.class.getDeclaredField("createDatetime");
        Field maxWithdrawalAmount = ConfigState.class.getDeclaredField("maxWithdrawalAmount");
        Field currency = ConfigState.class.getDeclaredField("currency");
        Field participants = ConfigState.class.getDeclaredField("participants");

        assertTrue(creator.getType().isAssignableFrom(String.class));
        assertTrue(country.getType().isAssignableFrom(String.class));
        assertTrue(createDatetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(maxWithdrawalAmount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
    }

    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){
        assertEquals(configState.getCreator(), "US DoJ");
        assertEquals(configState.getCountry(), "Catan");
        assertEquals(configState.getCreateDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertTrue(configState.getMaxWithdrawalAmount().compareTo(BigDecimal.valueOf(999999)) > 0);
        assertEquals(configState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(configState.getParticipants(),new ArrayList<>(participants));
    }


}
