package com.newamerica.webserver;

import com.newamerica.flows.IssueFundFlow;
import com.newamerica.flows.IssueRequestFlow;
import com.newamerica.flows.ReceiveFundFlow;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import com.newamerica.webserver.dtos.Fund;
import com.newamerica.webserver.dtos.Request;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.PageSpecification;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_NUM;

/**
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class CommonController extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(CommonController.class);

    public CommonController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @GetMapping(value = "hello", produces = "text/plain")
    private String hello() {
        return "Hello OARS!";
    }

    @GetMapping(value = "nodeInfo", produces = "application/json")
    private ResponseEntity<String> getNodeInfo() {
        return new ResponseEntity<>(rpcOps.nodeInfo().getLegalIdentities().toString(), HttpStatus.OK);
    }

    @GetMapping(value = "me", produces = "application/json")
    private ResponseEntity<Party> getMyIdentity() {
        Party me = rpcOps.nodeInfo().getLegalIdentities().get(0);
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

    @GetMapping(value = "notary", produces = "application/json")
    private ResponseEntity<List<Party>> getNotary() {
        List<Party> notaries = rpcOps.notaryIdentities();
        return ResponseEntity.ok(notaries);
    }

}