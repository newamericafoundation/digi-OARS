package com.newamerica.state;

import com.newamerica.states.FundState;
import net.corda.core.identity.Party;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.newamerica.TestUtils.CATAN;
import static com.newamerica.TestUtils.US;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FundStateTests {
    private FundState fundState;
    private List<Party> owners = new ArrayList<>();
    private List<Party> requiredSigners = new ArrayList<>();


    @Before
    public void setup(){
        owners.add(US.getParty());
        requiredSigners.add(US.getParty());
        requiredSigners.add(CATAN.getParty());

        fundState = new FundState(
                US.getParty(),
                CATAN.getParty(),
                owners,
                requiredSigners,
                5000000,
                5000000,
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                1000000,
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED
        );

    }

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field originCountry = FundState.class.getDeclaredField("originCountry");
        Field targetCountry = FundState.class.getDeclaredField("targetCountry");
        Field owners = FundState.class.getDeclaredField("owners");
        Field requiredSigners = FundState.class.getDeclaredField("requiredSigners");
        Field amount = FundState.class.getDeclaredField("amount");
        Field balance = FundState.class.getDeclaredField("balance");
        Field datetime = FundState.class.getDeclaredField("datetime");
        Field maxWithdrawalAmount = FundState.class.getDeclaredField("maxWithdrawalAmount");
        Field currency = FundState.class.getDeclaredField("currency");
        Field status = FundState.class.getDeclaredField("status");

        assertTrue(originCountry.getType().isAssignableFrom(Party.class));
        assertTrue(targetCountry.getType().isAssignableFrom(Party.class));
        assertTrue(owners.getType().isAssignableFrom(List.class));
        assertTrue(requiredSigners.getType().isAssignableFrom(List.class));
        assertTrue(amount.getType().isAssignableFrom(double.class));
        assertTrue(balance.getType().isAssignableFrom(double.class));
        assertTrue(datetime.getType().isAssignableFrom(ZonedDateTime.class));
        assertTrue(maxWithdrawalAmount.getType().isAssignableFrom(double.class));
        assertTrue(currency.getType().isAssignableFrom(Currency.class));
        assertTrue(status.getType().isAssignableFrom(FundState.FundStateStatus.class));
    }

    // ensure all getter tests return data as expected
    @Test
    public void getterTests(){

        assertEquals(fundState.getOriginCountry(), US.getParty());
        assertEquals(fundState.getTargetCountry(), CATAN.getParty());
        assertEquals(fundState.getOwners(),owners);
        assertEquals(fundState.getRequiredSigners(), requiredSigners);
        assertTrue(fundState.getAmount() > 4999999);
        assertTrue(fundState.getBalance() > 4999999);
        assertEquals(fundState.getDatetime(), ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")));
        assertTrue(fundState.getMaxWithdrawalAmount() > 999999);
        assertEquals(fundState.getCurrency(), Currency.getInstance(Locale.US));
        assertEquals(fundState.getStatus(), FundState.FundStateStatus.ISSUED);

    }

    // ensure that the balance is properly reduced while using the withdraw() helper function
    @Test
    public void withdrawalHelperFunctionTest(){
        double newBalance = fundState.withdraw(1000000).getBalance();
        assertTrue(newBalance > 3999999 && newBalance < 4000001);
    }

}
