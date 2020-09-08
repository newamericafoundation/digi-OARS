package com.newamerica.webserver;

import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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