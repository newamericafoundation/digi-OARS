package com.newamerica.state;

import com.newamerica.states.FundState;
import net.corda.core.identity.Party;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.Locale;

import static com.newamerica.TestUtils.CATAN;
import static com.newamerica.TestUtils.US;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FundStateTests {

    // ensure that the FundState object has all necessary attributes and correct data types.
    @Test
    public void hasAllAttributes() throws NoSuchFieldException{
        Field originCountry = FundState.class.getDeclaredField("originCountry");
        Field targetCountry = FundState.class.getDeclaredField("targetCountry");
        Field amount = FundState.class.getDeclaredField("amount");
        Field balance = FundState.class.getDeclaredField("balance");
        Field datetime = FundState.class.getDeclaredField("datetime");
        Field maxWithdrawalAmount = FundState.class.getDeclaredField("maxWithdrawalAmount");
        Field currency = FundState.class.getDeclaredField("currency");
        Field status = FundState.class.getDeclaredField("status");

        assertTrue(originCountry.getType().isAssignableFrom(Party.class));
        assertTrue(targetCountry.getType().isAssignableFrom(Party.class));
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
        FundState fundState = new FundState(
                US.getParty(),
                CATAN.getParty(),
                5000000,
                5000000,
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                1000000,
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED
        );

        assertEquals(fundState.getOriginCountry(), US.getParty());
        assertEquals(fundState.getTargetCountry(), CATAN.getParty());
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
        FundState fundState = new FundState(
                US.getParty(),
                CATAN.getParty(),
                5000000,
                5000000,
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                1000000,
                Currency.getInstance(Locale.US),
                FundState.FundStateStatus.ISSUED
        );
        double newBlance = fundState.withdraw(1000000).getBalance();
        assertTrue(newBlance > 3999999 && newBlance < 4000001);
    }

}
