package com.newamerica;

import com.google.common.collect.ImmutableList;
import com.newamerica.flows.IssueFundFlow;
import com.newamerica.states.FundState;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.TestCordapp;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static net.corda.testing.driver.Driver.driver;
import static org.junit.Assert.assertEquals;

public class DriverBasedTest {
    //testing constants
    private final CordaX500Name US_DOS = new CordaX500Name("US_DoS", "New York", "US");
    private final CordaX500Name US_DOJ = new CordaX500Name("US_DoJ", "New York", "US");
    private final CordaX500Name US_CSO = new CordaX500Name("US_CSO", "New York", "US");
    private final CordaX500Name US_TREASURY = new CordaX500Name("usTreasury", "New York", "US");
    private final CordaX500Name NEW_AMERICA = new CordaX500Name("NewAmerica", "New York", "US");
    private final CordaX500Name CATAN_MOF = new CordaX500Name("Catan_MoFA", "London", "GB");
    private final CordaX500Name CATAN_MOJ = new CordaX500Name("Catan_MoJ", "London", "GB");
    private final CordaX500Name CATAN_CSO = new CordaX500Name("Catan_CSO1", "London", "GB");
    private final CordaX500Name CATAN_TREASURY = new CordaX500Name("Catan_Treasury", "London", "GB");
    List<TestCordapp> CORDAPPS = new ArrayList<>();
    @Before
    public void setup() {
        Map<String, String> config = new HashMap<>();
        config.put("notary", "O=Notary Service,L=Zurich,C=CH");


        CORDAPPS.add(TestCordapp.findCordapp("com.newamerica.contracts"));
        CORDAPPS.add(TestCordapp.findCordapp("com.newamerica.states"));
        CORDAPPS.add(TestCordapp.findCordapp("com.newamerica.flows").withConfig(config));
    }

    @Test
    public void nodeTest() {
        driver(new DriverParameters().withStartNodesInProcess(true).withCordappsForAllNodes(CORDAPPS), dsl -> {
            // Start all nodes and wait for them to be ready.
            List<CordaFuture<NodeHandle>> handleFutures = ImmutableList.of(
                    dsl.startNode(new NodeParameters().withProvidedName(US_DOS)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_DOJ)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_CSO)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_TREASURY)),
                    dsl.startNode(new NodeParameters().withProvidedName(NEW_AMERICA)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_MOF)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_MOJ)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_CSO)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_TREASURY))
            );

            try {
                NodeHandle usDOSHandle = handleFutures.get(0).get();
                NodeHandle usDOJHandle = handleFutures.get(1).get();
                NodeHandle usCSOHandle = handleFutures.get(2).get();
                NodeHandle usTreasuryHandle = handleFutures.get(3).get();
                NodeHandle newAmericaHandle = handleFutures.get(4).get();
                NodeHandle catanMOFHandle = handleFutures.get(5).get();
                NodeHandle catanMOJHandle = handleFutures.get(6).get();
                NodeHandle catanCSOHandle = handleFutures.get(7).get();
                NodeHandle catanTreasuryHandle = handleFutures.get(8).get();

                // From each node, make an RPC call to retrieve another node's name from the network map, to verify that the
                // nodes have started and can communicate.

                // This is a very basic test: in practice tests would be starting flows, and verifying the states in the vault
                // and other important metrics to ensure that your CorDapp is working as intended.
                assertEquals(usDOSHandle.getRpc().wellKnownPartyFromX500Name(CATAN_MOF).getName(), CATAN_MOF);
                assertEquals(catanMOFHandle.getRpc().wellKnownPartyFromX500Name(US_DOS).getName(), US_DOS);
            } catch (Exception e) {
                throw new RuntimeException("Caught exception during test: ", e);
            }

            return null;
        });
    }

    @Test
    public void createFundState() {
        driver(new DriverParameters().withStartNodesInProcess(true).withCordappsForAllNodes(CORDAPPS), dsl -> {
            // Start all nodes and wait for them to be ready.
            List<CordaFuture<NodeHandle>> handleFutures = ImmutableList.of(
                    dsl.startNode(new NodeParameters().withProvidedName(US_DOS)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_DOJ)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_CSO)),
                    dsl.startNode(new NodeParameters().withProvidedName(US_TREASURY)),
                    dsl.startNode(new NodeParameters().withProvidedName(NEW_AMERICA)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_MOF)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_MOJ)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_CSO)),
                    dsl.startNode(new NodeParameters().withProvidedName(CATAN_TREASURY))
            );

            try {
                NodeHandle usDOSHandle = handleFutures.get(0).get();
                NodeHandle usDOJHandle = handleFutures.get(1).get();
                NodeHandle usCSOHandle = handleFutures.get(2).get();
                NodeHandle usTreasuryHandle = handleFutures.get(3).get();
                NodeHandle newAmericaHandle = handleFutures.get(4).get();
                NodeHandle catanMOFHandle = handleFutures.get(5).get();
                NodeHandle catanMOJHandle = handleFutures.get(6).get();
                NodeHandle catanCSOHandle = handleFutures.get(7).get();
                NodeHandle catanTreasuryHandle = handleFutures.get(8).get();

                CordaRPCClient usDOSClient = new CordaRPCClient(usDOSHandle.getRpcAddress());
                CordaRPCOps usDOSProxy = usDOSClient.start("default", "default").getProxy();

                CordaRPCClient catanMOJClient = new CordaRPCClient(catanMOFHandle.getRpcAddress());
                CordaRPCOps catanMOFProxy = catanMOJClient.start("default", "default").getProxy();

                Party usDOSParty = usDOSProxy.wellKnownPartyFromX500Name(US_DOS);
                Party usDOJParty = usDOSProxy.wellKnownPartyFromX500Name(US_DOJ);
                Party usCSOParty = usDOSProxy.wellKnownPartyFromX500Name(US_CSO);
                Party usTreasuryParty = usDOSProxy.wellKnownPartyFromX500Name(US_TREASURY);
                Party newAmericaParty = usDOSProxy.wellKnownPartyFromX500Name(NEW_AMERICA);
                Party catanMOFParty = usDOSProxy.wellKnownPartyFromX500Name(CATAN_MOF);
                Party catanMOJParty = usDOSProxy.wellKnownPartyFromX500Name(CATAN_MOJ);
                Party catanCSOParty = usDOSProxy.wellKnownPartyFromX500Name(CATAN_CSO);
                Party catanTreasuryParty = usDOSProxy.wellKnownPartyFromX500Name(CATAN_TREASURY);

                List<Party> owners = new ArrayList<>();
                owners.add(usDOSParty);

                List<Party> requiredSigners = new ArrayList<>();
                requiredSigners.add(usDOSParty);
                requiredSigners.add(catanMOFParty);

                List<Party> participants = new ArrayList<>();
                participants.add(usDOSParty);
                participants.add(usDOJParty);
                participants.add(usCSOParty);
                participants.add(usTreasuryParty);
                participants.add(newAmericaParty);
                participants.add(catanMOFParty);
                participants.add(catanMOJParty);
                participants.add(catanCSOParty);
                participants.add(catanTreasuryParty);


                List<Party> partialRequestParticipants = new ArrayList<>();
                partialRequestParticipants.add(usCSOParty);
                partialRequestParticipants.add(catanCSOParty);

                usDOSProxy.startFlowDynamic(IssueFundFlow.InitiatorFlow.class,
                        usDOSProxy.wellKnownPartyFromX500Name(US_DOS),
                        usDOSProxy.wellKnownPartyFromX500Name(CATAN_MOF),
                        owners,
                        requiredSigners,
                        partialRequestParticipants,
                        BigDecimal.valueOf(5000000),
                        ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")),
                        BigDecimal.valueOf(1000000),
                        Currency.getInstance(Locale.US),
                        participants
                        ).getReturnValue().get();

                //make sure that the OriginParty (usDOSParty) node has the issued state in its vault
                List<StateAndRef<FundState>> fundStatesUSDOS =  usDOSProxy.vaultQuery(FundState.class).getStates();
                FundState issuedFundState = fundStatesUSDOS.get(0).getState().getData();

                assertEquals(1, fundStatesUSDOS.size());
                assertEquals(usDOSParty, issuedFundState.getOriginParty());
                assertEquals(catanMOFParty, issuedFundState.getReceivingParty());
                assertEquals(owners, issuedFundState.getOwners());
                assertEquals(requiredSigners, issuedFundState.getRequiredSigners());
                assertEquals(partialRequestParticipants, issuedFundState.getPartialRequestParticipants());
                assertEquals(BigDecimal.valueOf(5000000), issuedFundState.getAmount());
                assertEquals(ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")), issuedFundState.getDatetime());
                assertEquals(BigDecimal.valueOf(1000000), issuedFundState.getMaxWithdrawalAmount());
                assertEquals(Currency.getInstance(Locale.US), issuedFundState.getCurrency());
                assertEquals(participants, issuedFundState.getParticipants());

                //make sure that the ReceivingParty (catanMOFParty) node has the issued state in its vault
                List<StateAndRef<FundState>> fundStatesCatanMOF =  catanMOFProxy.vaultQuery(FundState.class).getStates();
                FundState issuedFundStateCatanMOF = fundStatesCatanMOF.get(0).getState().getData();

                assertEquals(1, fundStatesCatanMOF.size());
                assertEquals(usDOSParty, issuedFundStateCatanMOF.getOriginParty());
                assertEquals(catanMOFParty, issuedFundStateCatanMOF.getReceivingParty());
                assertEquals(owners, issuedFundStateCatanMOF.getOwners());
                assertEquals(requiredSigners, issuedFundStateCatanMOF.getRequiredSigners());
                assertEquals(partialRequestParticipants, issuedFundStateCatanMOF.getPartialRequestParticipants());
                assertEquals(BigDecimal.valueOf(5000000), issuedFundStateCatanMOF.getAmount());
                assertEquals(ZonedDateTime.of(2020, 6, 27, 10, 30, 30, 0, ZoneId.of("America/New_York")), issuedFundStateCatanMOF.getDatetime());
                assertEquals(BigDecimal.valueOf(1000000), issuedFundStateCatanMOF.getMaxWithdrawalAmount());
                assertEquals(Currency.getInstance(Locale.US), issuedFundStateCatanMOF.getCurrency());
                assertEquals(participants, issuedFundStateCatanMOF.getParticipants());
            } catch (Exception e) {
                throw new RuntimeException("Caught exception during test: ", e);
            }
            return null;
        });
    }
}