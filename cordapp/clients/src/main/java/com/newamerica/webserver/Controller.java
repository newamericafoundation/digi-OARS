package com.newamerica.webserver;

import com.newamerica.flows.IssueFundFlow;
import com.newamerica.states.FundState;
import com.newamerica.webserver.dtos.Fund;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
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
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_NUM;
import static sun.misc.Version.println;

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class Controller extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(Controller.class);

    public Controller(NodeRPCConnection rpc) {
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

    @PostMapping(value = "fund", consumes = "application/json", produces = "application/json")
    private Response createFund (@Valid @RequestBody Fund request) {
        try {
            String resourcePath = "/fund";

            String originPartyName = request.getOriginParty();
            String receivingPartyName = request.getReceivingParty();
            String amountStr = request.getAmount();
            String maxWithdrawalAmountStr = request.getMaxWithdrawalAmount();

            Party originParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(originPartyName));
            Party receivingParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(receivingPartyName));
            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US,OU=DoS,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US,OU=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan,OU=MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan,OU=MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan,OU=Treasury,L=London,C=GB"));
            Party Catan_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan,OU=CSO,L=London,C=GB"));
            Party US_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US,OU=CSO,L=New York,C=US"));

            BigDecimal amountAndBalance = new BigDecimal(amountStr);
            ZonedDateTime now = ZonedDateTime.now();
            BigDecimal maxWithdrawalAmount = new BigDecimal(maxWithdrawalAmountStr);
            Currency currency = Currency.getInstance("USD");

            List<AbstractParty> owners = Arrays.asList(originParty);
            List<AbstractParty> requiredSigners =  Arrays.asList(originParty, receivingParty);
            List<AbstractParty> partialRequestParticipants = Arrays.asList(Catan_CSO, US_CSO);
            List<AbstractParty> participants = Arrays.asList(originParty, US_DoS, NewAmerica, Catan_MoFA, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueFundFlow.InitiatorFlow.class,
                    originParty,
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
            return Response.ok(createSuccessServiceResponse("Fund created successfully.", request, resourcePath)).build();
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @Path("/funds")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    private Response getAllFunds () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.ALL);
            List<StateAndRef<FundState>> fundList = rpcOps.vaultQueryByWithPagingSpec(FundState.class, queryCriteria, pagingSpec).getStates();
            return Response.ok(fundList).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @Path("/fund/{fundId}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    private Response getFundById (@PathParam("fundId") String fundId) {
        try {
            String resourcePath = String.format("/fund/%s", fundId);
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(UUID.fromString(fundId)));
            StateAndRef<FundState> fund = rpcOps.vaultQueryByWithPagingSpec(FundState.class, queryCriteria, pagingSpec).getStates().get(0);
            return Response.ok(fund.getState().getData()).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GET
    @Path("/fund/{status}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    private Response getFundByStatus (@PathParam("status") String status) {
        try {
            String resourcePath = String.format("/fund/%s", status);
            FundState.FundStateStatus fundStateStatus = FundState.FundStateStatus.valueOf(status);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.ALL);
            List<StateAndRef<FundState>> funds = rpcOps.vaultQueryByCriteria(queryCriteria, FundState.class).getStates();
            List<FundState> result = funds.stream().filter(it -> it.getState().getData().getStatus().equals(fundStateStatus)).map(it -> it.getState().getData()).collect(Collectors.toList());
            return Response.ok(result).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}