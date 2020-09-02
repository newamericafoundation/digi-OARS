package com.newamerica.flow;

import com.newamerica.contracts.PartialRequestContract;
import com.newamerica.flows.IssuePartialRequestFundFlow;
import com.newamerica.states.PartialRequestState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.contracts.UniqueIdentifier;
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
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertEquals;

public class IssuePartialRequestFundFlowTests {

    private MockNetwork mockNetwork;
    private StartedMockNode a, b, c;
    private Party catanMoj;
    private Party usCso;
    private Party catanCso;
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> authorizedParties = new ArrayList<>();

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
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssuePartialRequestFundFlow.ResponderFlow.class));
        mockNetwork.runNetwork();

        catanMoj = a.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        usCso = b.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        catanCso = c.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();


        participants.add(usCso);
        participants.add(catanCso);
        authorizedParties.add(catanMoj);
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
        IssuePartialRequestFundFlow.InitiatorFlow flow = new IssuePartialRequestFundFlow.InitiatorFlow(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(flow);
        mockNetwork.runNetwork();

        // Return the unsigned(!) SignedTransaction object from the IssuePartialRequestFlow.
        SignedTransaction ptx = future.get();


        // Check the transaction is well formed...
        // No outputs, one input PartialRequestState and a command with the right properties.
        assert (ptx.getTx().getInputs().isEmpty());
        assert (ptx.getTx().getOutputs().get(0).getData() instanceof PartialRequestState);

        Command command = ptx.getTx().getCommands().get(0);
        assert (command.getValue() instanceof PartialRequestContract.Commands.Issue);

        ptx.verifySignaturesExcept(catanMoj.getOwningKey(),
                mockNetwork.getDefaultNotaryNode().getInfo().getLegalIdentitiesAndCerts().get(0).getOwningKey());
    }

    @Test
    public void flowReturnsVerifiedPartiallySignedTransaction() throws Exception {

        //should fail because:The amount must be greater than or equal to zero.
        IssuePartialRequestFundFlow.InitiatorFlow negativeAmountValue = new IssuePartialRequestFundFlow.InitiatorFlow(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(-1),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                participants
        );

        Future<SignedTransaction> futureOne = a.startFlow(negativeAmountValue);
        mockNetwork.runNetwork();
        exception.expectCause(instanceOf(TransactionVerificationException.class));
        futureOne.get();

        //should verify
        IssuePartialRequestFundFlow.InitiatorFlow validPartialRequestState = new IssuePartialRequestFundFlow.InitiatorFlow(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                participants
        );

        Future<SignedTransaction> futureTwo = a.startFlow(validPartialRequestState);
        mockNetwork.runNetwork();
        futureTwo.get();
    }

    // all signatures were properly fetched.
    @Test
    public void flowReturnsTransactionSignedByBothParties() throws Exception {

        IssuePartialRequestFundFlow.InitiatorFlow validPartialRequestState = new IssuePartialRequestFundFlow.InitiatorFlow(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(validPartialRequestState);
        mockNetwork.runNetwork();

        SignedTransaction stx = future.get();
        stx.verifyRequiredSignatures();
    }

    // check each party's vault for the fundState's existence
    @Test
    public void flowRecordsTheSameTransactionInBothPartyVaults() throws Exception {

        IssuePartialRequestFundFlow.InitiatorFlow validPartialRequestState = new IssuePartialRequestFundFlow.InitiatorFlow(
                "Catan Ministry of Education",
                authorizedParties,
                BigDecimal.valueOf(1000000),
                Currency.getInstance("USD"),
                ZonedDateTime.now(),
                new UniqueIdentifier(),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(validPartialRequestState);
        mockNetwork.runNetwork();
        SignedTransaction stx = future.get();
        System.out.printf("Signed transaction hash: %h\n", stx.getId());

        Stream.of(a, b).map(el ->
                el.getServices().getValidatedTransactions().getTransaction(stx.getId())
        ).filter(Objects::nonNull).forEach(el -> {
            SecureHash txHash = el.getId();
            System.out.printf("$txHash == %h\n", stx.getId());
            assertEquals(stx.getId(), txHash);
        });
    }
}