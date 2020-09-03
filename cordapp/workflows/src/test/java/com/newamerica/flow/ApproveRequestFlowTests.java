package com.newamerica.flow;

import com.newamerica.contracts.RequestContract;
import com.newamerica.flows.ApproveRequestFlow;
import com.newamerica.flows.IssueFundFlow;
import com.newamerica.flows.IssueRequestFlow;
import com.newamerica.flows.ReceiveFundFlow;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import net.corda.core.contracts.Command;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ApproveRequestFlowTests {

    private MockNetwork mockNetwork;
    private StartedMockNode a;
    private StartedMockNode b;
    private Party usDoj;
    SignedTransaction stx4;
    private final List<AbstractParty> owners = new ArrayList<>();
    private final List<AbstractParty> requiredSigners = new ArrayList<>();
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> partialRequestParticipants = new ArrayList<>();



    @Before
    public void setup() throws ExecutionException, InterruptedException {
        Map<String, String> config = new HashMap<>();
        config.put("notary", "O=Notary,L=London,C=GB");
        MockNetworkParameters mockNetworkParameters = new MockNetworkParameters().withCordappsForAllNodes(
                Arrays.asList(
                        TestCordapp.findCordapp("com.newamerica.contracts"),
                        TestCordapp.findCordapp("com.newamerica.flows").withConfig(config)
                )
        ).withNotarySpecs(Collections.singletonList(new MockNetworkNotarySpec(new CordaX500Name("Notary", "London", "GB"))));
        mockNetwork = new MockNetwork(mockNetworkParameters);
        System.out.println(mockNetwork);

        a = mockNetwork.createNode(new MockNodeParameters());
        b = mockNetwork.createNode(new MockNodeParameters());
        StartedMockNode c = mockNetwork.createNode(new MockNodeParameters());
        StartedMockNode d = mockNetwork.createNode(new MockNodeParameters());
        StartedMockNode e = mockNetwork.createNode(new MockNodeParameters());
        StartedMockNode f = mockNetwork.createNode(new MockNodeParameters());

        ArrayList<StartedMockNode> startedNodes = new ArrayList<>();
        startedNodes.add(a);
        startedNodes.add(b);
        startedNodes.add(c);
        startedNodes.add(d);
        startedNodes.add(e);
        startedNodes.add(f);

        // For real nodes this happens automatically, but we have to manually register the flow for tests
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueFundFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueRequestFlow.ExtraInitiatingFlowResponder.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueRequestFlow.CollectSignaturesResponder.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueRequestFlow.CollectSignaturesResponder.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(ApproveRequestFlow.ResponderFlow.class));

        mockNetwork.runNetwork();

        Party usDos = a.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        usDoj = b.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanMof = c.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanMoj = d.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party usCSO = e.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanCSO = f.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();

        owners.add(usDos);
        requiredSigners.add(usDoj);
        requiredSigners.add(catanMoj);
        participants.add(usDos);
        participants.add(usDoj);
        participants.add(catanMof);
        participants.add(catanMoj);
        partialRequestParticipants.add(usCSO);
        partialRequestParticipants.add(catanCSO);

        //create FundState
        IssueFundFlow.InitiatorFlow fundStateFlow = new IssueFundFlow.InitiatorFlow(
                usDos,
                catanMoj,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(fundStateFlow);
        mockNetwork.runNetwork();
        SignedTransaction stx = future.get();
        FundState fs = (FundState) stx.getTx().getOutputStates().get(0);

        //acknowledge the FundState
        ReceiveFundFlow.InitiatorFlow receiveFundFlow = new ReceiveFundFlow.InitiatorFlow(
                fs.getLinearId()
        );
        Future<SignedTransaction> futureTwo = a.startFlow(receiveFundFlow);
        mockNetwork.runNetwork();
        futureTwo.get();

        //create RequestState
        IssueRequestFlow.InitiatorFlow requestFlow = new IssueRequestFlow.InitiatorFlow(
                "Alice Bob",
                "Catan Ministry of Education",
                "Chris Jones",
                "1234567890",
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                ZonedDateTime.of(2020, 6, 27, 10,30,30,0, ZoneId.of("America/New_York")),
                fs.getLinearId(),
                participants
        );

        Future<SignedTransaction> futureThree = c.startFlow(requestFlow);
        mockNetwork.runNetwork();
        SignedTransaction stx3 = futureThree.get();
        RequestState rs = (RequestState) stx3.getTx().getOutputStates().get(0);

        //approve requestState
        ApproveRequestFlow.InitiatorFlow approveRequestFlow = new ApproveRequestFlow.InitiatorFlow(
                rs.getLinearId()
        );

        Future<SignedTransaction> futureFour = d.startFlow(approveRequestFlow);
        mockNetwork.runNetwork();
        stx4 = futureFour.get();
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

        // Check the transaction is well formed...
        // No outputs, one input IOUState and a command with the right properties.
        assert (!stx4.getTx().getInputs().isEmpty());
        assert (stx4.getTx().getOutputs().get(0).getData() instanceof RequestState);

        Command command = stx4.getTx().getCommands().get(0);
        assert (command.getValue() instanceof RequestContract.Commands.Approve);

        stx4.verifySignaturesExcept(usDoj.getOwningKey(),
                mockNetwork.getDefaultNotaryNode().getInfo().getLegalIdentitiesAndCerts().get(0).getOwningKey());
    }

    // all signatures were properly fetched.
    @Test
    public void flowReturnsTransactionSignedByBothParties() throws Exception {
        stx4.verifyRequiredSignatures();
    }

    // check each party's vault for the requestState's existence
    @Test
    public void flowRecordsTheSameTransactionInBothPartyVaults() {

        Stream.of(a, b).map(el ->
                el.getServices().getValidatedTransactions().getTransaction(stx4.getId())
        ).filter(Objects::nonNull).forEach(el -> {
            SecureHash txHash = el.getId();
            System.out.printf("$txHash == %h\n", stx4.getId());
            assertEquals(stx4.getId(), txHash);
        });
    }
}