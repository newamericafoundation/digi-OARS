package com.newamerica.state;

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

public class FundStateTests {
    private FundState fundState;
    private final List<AbstractParty> owners = new ArrayList<>();
    private final List<AbstractParty> requiredSigners = new ArrayList<>();
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> partialRequestParticipants = new ArrayList<>();


    @Before
    public void setup(){
        owners.add(US.getParty());
        requiredSigners.add(US.getParty());
        requiredSigners.add(CATAN.getParty());
        participants.add(US.getParty());
        participants.add(CATAN.getParty());
        partialRequestParticipants.add(US_CSO.getParty());
        partialRequestParticipants.add(CATAN_CSO.getParty());


        fundState = new FundState(
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
    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field originCountry = FundState.class.getDeclaredField("originCountry");
        Field receivingCountry = FundState.class.getDeclaredField("receivingCountry");
        Field owners = FundState.class.getDeclaredField("owners");
        Field requiredSigners = FundState.class.getDeclaredField("requiredSigners");
        Field partialRequestParticipants = FundState.class.getDeclaredField("partialRequestParticipants");
        Field amount = FundState.class.getDeclaredField("amount");
        Field balance = FundState.class.getDeclaredField("balance");
        Field datetime = FundState.class.getDeclaredField("datetime");
        Field maxWithdrawalAmount = FundState.class.getDeclaredField("maxWithdrawalAmount");
        Field currency = FundState.class.getDeclaredField("currency");
        Field status = FundState.class.getDeclaredField("status");
        Field participants = FundState.class.getDeclaredField("participants");

        assertTrue(originCountry.getType().isAssignableFrom(Party.class));
        assertTrue(receivingCountry.getType().isAssignableFrom(Party.class));
        assertTrue(owners.getType().isAssignableFrom(List.class));
        assertTrue(requiredSigners.getType().isAssignableFrom(List.class));
        assertTrue(partialRequestParticipants.getType().isAssignableFrom(List.class));
        assertTrue(amount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(balance.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(datetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(maxWithdrawalAmount.getType().isAssignableFrom(BigDecimal.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(status.getType().isAssignableFrom(FundState.FundStateStatus.class));
        assertTrue(participants.getType().isAssignableFrom(List.class));
    }

    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){

        assertEquals(fundState.getOriginCountry(), US.getParty());
        assertEquals(fundState.getReceivingCountry(), CATAN.getParty());
        assertEquals(fundState.getOwners(),owners);
        assertEquals(fundState.getRequiredSigners(), requiredSigners);
        assertEquals(fundState.getPartialRequestParticipants(), partialRequestParticipants);
        assertTrue(fundState.getAmount().compareTo(BigDecimal.valueOf(4999999)) > 0);
        assertTrue(fundState.getBalance().compareTo(BigDecimal.valueOf(4999999)) > 0);
        assertEquals(fundState.getDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertTrue(fundState.getMaxWithdrawalAmount().compareTo(BigDecimal.valueOf(999999)) > 0);
        assertEquals(fundState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(fundState.getStatus(), FundState.FundStateStatus.ISSUED);
        assertEquals(fundState.getParticipants(),new ArrayList<>(participants));

    }

    // ensure that the balance is properly reduced while using the withdraw() helper function
    @Test
    public void withdrawalHelperFunctionTest(){
        BigDecimal newBalance = fundState.withdraw(BigDecimal.valueOf(1000000)).getBalance();
        assertTrue(newBalance.compareTo(BigDecimal.valueOf(3999999)) > 0  && newBalance.compareTo(BigDecimal.valueOf(4000001)) < 0);
    }

}
