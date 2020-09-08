package com.newamerica.flow;

import com.newamerica.contracts.TransferContract;
import com.newamerica.flows.*;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import com.newamerica.states.TransferState;
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

import static com.newamerica.TestUtils.CATANMoFA;
import static com.newamerica.TestUtils.CATANMoJ;
import static org.junit.Assert.assertEquals;

public class IssueTransferFlowTests {
    private MockNetwork mockNetwork;
    private StartedMockNode a;
    private StartedMockNode b;
    private StartedMockNode c;
    private StartedMockNode d;
    private StartedMockNode e;
    private StartedMockNode f;
    private StartedMockNode g;

    SignedTransaction stx5;
    Party catanTreasury;
    private final List<AbstractParty> owners = new ArrayList<>();
    private final List<AbstractParty> requiredSigners = new ArrayList<>();
    private final List<AbstractParty> participants = new ArrayList<>();
    private final List<AbstractParty> partialRequestParticipants = new ArrayList<>();
    private final List<AbstractParty> authorizedParties = new ArrayList<>();



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

        a = mockNetwork.createNode(new MockNodeParameters());
        b = mockNetwork.createNode(new MockNodeParameters());
        c = mockNetwork.createNode(new MockNodeParameters());
        d = mockNetwork.createNode(new MockNodeParameters());
        e = mockNetwork.createNode(new MockNodeParameters());
        f = mockNetwork.createNode(new MockNodeParameters());
        g = mockNetwork.createNode(new MockNodeParameters());

        ArrayList<StartedMockNode> startedNodes = new ArrayList<>();
        startedNodes.add(a);
        startedNodes.add(b);
        startedNodes.add(c);
        startedNodes.add(d);
        startedNodes.add(e);
        startedNodes.add(f);
        startedNodes.add(g);

        // For real nodes this happens automatically, but we have to manually register the flow for tests
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueFundFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueRequestFlow.ExtraInitiatingFlowResponder.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueRequestFlow.CollectSignaturesResponder.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssuePartialRequestFundFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(UpdateFundBalanceFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(ReceiveFundFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(ApproveRequestFlow.ResponderFlow.class));
        startedNodes.forEach(el -> el.registerInitiatedFlow(IssueTransferFlow.ResponderFlow.class));

        mockNetwork.runNetwork();

        Party usDos = a.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party usDoj = b.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanMof = c.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanMoj = d.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party usCSO = e.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        Party catanCSO = f.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();
        catanTreasury = g.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();


        owners.add(usDoj);
        requiredSigners.add(usDoj);
        requiredSigners.add(catanMoj);
        participants.add(usDos);
        participants.add(usDoj);
        participants.add(catanMof);
        participants.add(catanMoj);
        participants.add(catanTreasury);
        partialRequestParticipants.add(usCSO);
        partialRequestParticipants.add(catanCSO);
        authorizedParties.add(CATANMoJ.getParty());
        authorizedParties.add(CATANMoFA.getParty());

        //create FundState
        IssueFundFlow.InitiatorFlow fundStateFlow = new IssueFundFlow.InitiatorFlow(
                usDoj,
                catanTreasury,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> future = b.startFlow(fundStateFlow);
        mockNetwork.runNetwork();
        SignedTransaction stx = future.get();
        FundState fs = (FundState) stx.getTx().getOutputStates().get(0);

        //acknowledge the FundState
        ReceiveFundFlow.InitiatorFlow receiveFundFlow = new ReceiveFundFlow.InitiatorFlow(
                fs.getLinearId()
        );
        Future<SignedTransaction> futureTwo = g.startFlow(receiveFundFlow);
        mockNetwork.runNetwork();
        futureTwo.get();

        //create RequestState
        IssueRequestFlow.InitiatorFlow requestFlow = new IssueRequestFlow.InitiatorFlow(
                "Alice Bob",
                "Catan Ministry of Education",
                "1234567890",
                authorizedParties,
                "build a school",
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
                rs.getLinearId(),
                "Chris Jones"
        );

        Future<SignedTransaction> futureFour = d.startFlow(approveRequestFlow);
        mockNetwork.runNetwork();
        futureFour.get();
        RequestState rs2 = (RequestState) stx3.getTx().getOutputStates().get(0);


        IssueTransferFlow.InitiatorFlow transferFlow = new IssueTransferFlow.InitiatorFlow(
                rs2.getLinearId(),
                participants
        );
        Future<SignedTransaction> futureFive = g.startFlow(transferFlow);
        mockNetwork.runNetwork();
        stx5= futureFive.get();
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
        assert (stx5.getTx().getInputs().isEmpty());
        assert (stx5.getTx().getOutputs().get(0).getData() instanceof TransferState);

        Command command = stx5.getTx().getCommands().get(0);
        assert (command.getValue() instanceof TransferContract.Commands.Issue);

        stx5.verifySignaturesExcept(catanTreasury.getOwningKey(),
                mockNetwork.getDefaultNotaryNode().getInfo().getLegalIdentitiesAndCerts().get(0).getOwningKey());
    }

    // check each party's vault for the TransferState's existence
    @Test
    public void flowRecordsTheSameTransactionInBothPartyVaults() {

        Stream.of(a, b, c, d, e, f, g).map(el ->
                el.getServices().getValidatedTransactions().getTransaction(stx5.getId())
        ).filter(Objects::nonNull).forEach(el -> {
            SecureHash txHash = el.getId();
            System.out.printf("$txHash == %h\n", stx5.getId());
            assertEquals(stx5.getId(), txHash);
        });
    }

}
