package com.newamerica.flow;

import com.newamerica.contracts.FundContract;
import com.newamerica.flows.IssueFundFlow;
import com.newamerica.states.FundState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;

public class IssueFundFlowTests {
    private MockNetwork mockNetwork;
    private StartedMockNode a, b, c;
    private Party usDos;
    private Party usDoj;
    private Party catan;
    private final List<AbstractParty> owners = new ArrayList<>();
    private final List<AbstractParty> requiredSigners = new ArrayList<>();
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> partialRequestParticipants = new ArrayList<>();


    @Before
    public void setup() {
        Map<String, String> config = new HashMap<>();
        config.put("notary", "O=Notary,L=London,C=GB");
        MockNetworkParameters mockNetworkParameters = new MockNetworkParameters().withCordappsForAllNodes(
                Arrays.asList(
                        TestCordapp.findCordapp("com.newamerica.contracts"),
                        TestCordapp.findCordapp("com.newamerica.flows").withConfig(config)
                )
        ).withNotarySpecs(Arrays.asList(new MockNetworkNotarySpec(new CordaX500Name("Notary", "London", "GB"))));
        mockNetwork = new MockNetwork(mockNetworkParameters);
        System.out.println(mockNetwork);

        a = mockNetwork.createNode(new MockNodeParameters());
        b = mockNetwork.createNode(new MockNodeParameters());
        c = mockNetwork.createNode(new MockNodeParameters());

        ArrayList<StartedMockNode> startedNodes = new ArrayList<>();
        startedNodes.add(a);
        startedNodes.add(b);
        startedNodes.add(c);

        // For real nodes this happens automatically, but we have to manually register the flow for tests
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueFundFlow.ResponderFlow.class));
        mockNetwork.runNetwork();

        usDos = a.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        usDoj = b.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        catan = c.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();

        owners.add(usDos);
        requiredSigners.add(usDos);
        requiredSigners.add(catan);
        participants.add(usDos);
        participants.add(usDoj);
        participants.add(catan);
        partialRequestParticipants.add(usDos);
        partialRequestParticipants.add(catan);
    }


    @After
    public void tearDown() {
        mockNetwork.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    // ensure that properly formed partially signed transactions are returned from the initiator flow
    @Test
    public void flowReturnsCorrectlyFormedPartiallySignedTransaction() throws Exception {

        IssueFundFlow.InitiatorFlow flow = new IssueFundFlow.InitiatorFlow(
                usDos,
                catan,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(flow);
        mockNetwork.runNetwork();

        // Return the unsigned(!) SignedTransaction object from the IOUIssueFlow.
        SignedTransaction ptx = future.get();

        // Print the transaction for debugging purposes.
        System.out.println(ptx.getTx());

        // Check the transaction is well formed...
        // No outputs, one input IOUState and a command with the right properties.
        assert (ptx.getTx().getInputs().isEmpty());
        assert (ptx.getTx().getOutputs().get(0).getData() instanceof FundState);

        Command command = ptx.getTx().getCommands().get(0);
        assert (command.getValue() instanceof FundContract.Commands.Issue);

        ptx.verifySignaturesExcept(usDoj.getOwningKey(),
                mockNetwork.getDefaultNotaryNode().getInfo().getLegalIdentitiesAndCerts().get(0).getOwningKey());
    }

    // All requirements should properly pass/fail according to the FundState contract.
    @Test
    public void flowReturnsVerifiedPartiallySignedTransaction() throws Exception {

        //should fail because:There must be at least one Party in the owner list.
        IssueFundFlow.InitiatorFlow originAndReceivingTheSame = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDos,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureOne = a.startFlow(originAndReceivingTheSame);
        mockNetwork.runNetwork();

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureOne.get();

        //should fail because: OriginCountry and ReceivingCountry cannot be the same Party.
        owners.clear();
        IssueFundFlow.InitiatorFlow emptyOwnersList = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureTwo = a.startFlow(emptyOwnersList);
        mockNetwork.runNetwork();

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureTwo.get();

        owners.add(usDos);

        //should fail because: There must be at least one Party in the requiredSigners list.
        requiredSigners.clear();
        IssueFundFlow.InitiatorFlow emptyRequiredSignersList = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureThree = a.startFlow(emptyRequiredSignersList);
        mockNetwork.runNetwork();

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureThree.get();

        requiredSigners.add(usDos);
        requiredSigners.add(catan);

        //should fail because: The amount must be greater than zero.
        IssueFundFlow.InitiatorFlow negativeAmountAndBalance = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureFour = a.startFlow(negativeAmountAndBalance);
        mockNetwork.runNetwork();

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureFour.get();

        //should fail because: The maxWithdrawalAmount must be greater than or equal to zero.
        IssueFundFlow.InitiatorFlow negativeMaxWithdrawalAmount = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureFive = a.startFlow(negativeMaxWithdrawalAmount);
        mockNetwork.runNetwork();

        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureFive.get();

        //should verify
        IssueFundFlow.InitiatorFlow validFundState = new IssueFundFlow.InitiatorFlow(
                usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> futureSix = a.startFlow(validFundState);
        mockNetwork.runNetwork();
        futureSix.get();
    }

    // all signatures were properly fetched.
    @Test
    public void flowReturnsTransactionSignedByBothParties() throws Exception {

        IssueFundFlow.InitiatorFlow flow = new IssueFundFlow.InitiatorFlow(usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants);

        Future<SignedTransaction> future = a.startFlow(flow);
        mockNetwork.runNetwork();

        SignedTransaction stx = future.get();
        stx.verifyRequiredSignatures();
    }

    // check each party's vault for the fundState's existence
    @Test
    public void flowRecordsTheSameTransactionInBothPartyVaults() throws Exception {

        IssueFundFlow.InitiatorFlow flow = new IssueFundFlow.InitiatorFlow(usDos,
                usDoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                Currency.getInstance(Locale.US),
                participants);

        Future<SignedTransaction> future = a.startFlow(flow);
        mockNetwork.runNetwork();
        SignedTransaction stx = future.get();
        System.out.printf("Signed transaction hash: %h\n", stx.getId());

        Stream.of(a, b).map(el ->
                el.getServices().getValidatedTransactions().getTransaction(stx.getId())
        ).forEach(el -> {
            SecureHash txHash = el.getId();
            System.out.printf("$txHash == %h\n", stx.getId());
            assertEquals(stx.getId(), txHash);
        });
    }
}
