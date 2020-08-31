package com.newamerica.flow;

import com.newamerica.contracts.FundContract;
import com.newamerica.flows.IssueFundFlow;
import com.newamerica.flows.ReceiveFundFlow;
import com.newamerica.states.FundState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.UniqueIdentifier;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ReceiveFundFlowTests {
    private MockNetwork mockNetwork;
    private StartedMockNode a, b, c;
    private Party usDos;
    private Party usDoj;
    private Party catanTreasury;
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
        ).withNotarySpecs(Arrays.asList(new MockNetworkNotarySpec(new CordaX500Name("Notary", "London", "GB"))));
        mockNetwork = new MockNetwork(mockNetworkParameters);

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
        catanTreasury = c.getInfo().getLegalIdentitiesAndCerts().get(0).getParty();

        owners.add(usDoj);
        requiredSigners.add(usDoj);
        requiredSigners.add(catanTreasury);
        participants.add(usDos);
        participants.add(usDoj);
        participants.add(catanTreasury);
        partialRequestParticipants.add(usDos);
        partialRequestParticipants.add(catanTreasury);
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
                usDoj,
                catanTreasury,
                owners,
                requiredSigners,
                partialRequestParticipants,
                BigDecimal.valueOf(5000000),
                ZonedDateTime.now(),
                BigDecimal.valueOf(1000000),
                Currency.getInstance(Locale.US),
                participants
        );

        Future<SignedTransaction> future = a.startFlow(flow);
        mockNetwork.runNetwork();

        // Return the unsigned(!) SignedTransaction object from the IOUIssueFlow.
        SignedTransaction ptx = future.get();

        // get fundstate linear id
        FundState input = (FundState) ptx.getTx().getOutputs().get(0).getData();
        UniqueIdentifier fundStateLinearId = input.getLinearId();
        System.out.println(fundStateLinearId);

        ReceiveFundFlow.InitiatorFlow receiveFlow = new ReceiveFundFlow.InitiatorFlow(
                fundStateLinearId
        );
        Future<SignedTransaction> futureTwo = c.startFlow(receiveFlow);
        mockNetwork.runNetwork();

        SignedTransaction ptx2 = futureTwo.get();
        FundState output = (FundState) ptx2.getTx().getOutputs().get(0).getData();
        System.out.println(output.toString());

        // Check the transaction is well formed...
        // one output, one input IOUState and a command with the right properties.
        assert (ptx2.getTx().getInputs().size() == 1);
        assert (ptx2.getTx().getOutputs().get(0).getData() instanceof FundState);

        Command command = ptx2.getTx().getCommands().get(0);
        assert (command.getValue() instanceof FundContract.Commands.Receive);

        ptx2.verifySignaturesExcept(catanTreasury.getOwningKey(),
                mockNetwork.getDefaultNotaryNode().getInfo().getLegalIdentitiesAndCerts().get(0).getOwningKey());
    }
}
