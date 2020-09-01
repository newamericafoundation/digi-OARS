package com.newamerica.webserver;

import com.newamerica.flows.IssueFundFlow;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeDiagnosticInfo;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import static jdk.nashorn.internal.objects.Global.println;

/**
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class Controller {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    private List<NodeInfo> removeAll(List<NodeInfo> network, NodeInfo node) {
        return null;
    }

    @GetMapping(value = "hello", produces = "text/plain")
    private String hello() {
        return "Hello OARS!";
    }

    @GetMapping(value = "nodeInfo", produces = "application/json")
    private ResponseEntity<String> getNodeInfo() {
        return new ResponseEntity<String>(rpcOps.nodeInfo().getLegalIdentities().toString(), HttpStatus.OK);
    }

    @GetMapping(value = "me", produces = "application/json")
    private ResponseEntity<NodeInfo> getMyIdentity() {
        NodeInfo me = rpcOps.nodeInfo();
        return ResponseEntity.ok(me);
    }

    @GetMapping(value = "flows", produces = "application/json")
    private ResponseEntity<String> getFlows() {
        List<String> flows = rpcOps.registeredFlows();
        return ResponseEntity.ok("Enumerate flows on this node: \n$flows");
    }

    @GetMapping(value = "version", produces = "application/json")
    private ResponseEntity<Integer> getVersion() {
        Integer version = rpcOps.nodeInfo().getPlatformVersion();
        return ResponseEntity.ok(version);
    }

    @GetMapping(value = "notary", produces = "application/json")
    private ResponseEntity<List<Party>> getNotary() {
        List<Party> notaries = rpcOps.notaryIdentities();
        return ResponseEntity.ok(notaries);
    }

    @GetMapping(value = "network", produces = "application/json")
    private ResponseEntity<List<Party>> getNetworkMap() {
        List<Party> filtered = rpcOps.networkMapSnapshot()
                .stream()
                .filter(e -> !e.equals(rpcOps.nodeInfo()))
                .flatMap(e -> e.getLegalIdentities().stream())
                .collect(Collectors.toList());
        filtered.remove(rpcOps.notaryIdentities().get(0));
        return ResponseEntity.ok(filtered);
    }

    @GetMapping(value = "diagnostics", produces = "application/json")
    private ResponseEntity<NodeDiagnosticInfo> getDiagnostics() {
        return ResponseEntity.ok(rpcOps.nodeDiagnosticInfo());
    }

    @PostMapping(value = "fund", produces = "application/json")
    private ResponseEntity<String> createFund(HttpServletRequest request) {
        String originPartyName = request.getParameter("originParty");
        String receivingPartyName = request.getParameter("receivingParty");
        String amountStr = request.getParameter("amount");
        String maxWithdrawalAmountStr = request.getParameter("maxWithdrawalAmount");

        Party originParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(originPartyName));
        Party receivingParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(receivingPartyName));
        Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=USDoS,L=New York,C=US"));
        Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
        Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=CatanMoJ,L=London,C=GB"));
        Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=CatanMoFA,L=London,C=GB"));
        Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=CatanTreasury,L=London,C=GB"));
        Party Catan_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=CatanCSO,L=London,C=GB"));
        Party US_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=USCSO,L=New York,C=US"));


        BigDecimal amountAndBalance = new BigDecimal(amountStr);
        ZonedDateTime now = ZonedDateTime.now();
        BigDecimal maxWithdrawalAmount = new BigDecimal(maxWithdrawalAmountStr);
        Currency currency = Currency.getInstance("USD");

        List<AbstractParty> owners = Arrays.asList(originParty);
        List<AbstractParty> requiredSigners =  Arrays.asList(originParty, receivingParty);
        List<AbstractParty> partialRequestParticipants = Arrays.asList(Catan_CSO, US_CSO);
        List<AbstractParty> participants = Arrays.asList(originParty, US_DoS, NewAmerica, Catan_MoFA, Catan_MoJ, Catan_Treasury);

        try {
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueFundFlow.InitiatorFlow.class,
                    receivingParty,
                    owners,
                    requiredSigners,
                    partialRequestParticipants,
                    amountAndBalance,
                    now,
                    maxWithdrawalAmount,
                    currency,
                    participants
            ).getReturnValue().get();
            return ResponseEntity.ok(tx.toString());
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}