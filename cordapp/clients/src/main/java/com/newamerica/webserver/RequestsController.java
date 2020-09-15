package com.newamerica.webserver;

import com.newamerica.flows.ApproveRequestFlow;
import com.newamerica.flows.IssueRequestFlow;
import com.newamerica.states.PartialRequestState;
import com.newamerica.states.RequestState;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
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
public class RequestsController extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(RequestsController.class);

    public RequestsController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @GetMapping(value = "/requests", produces = "application/json")
    private Response getAllRequests () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<RequestState>> requestList = rpcOps.vaultQueryByWithPagingSpec(RequestState.class, queryCriteria, pagingSpec).getStates();
            return Response.ok(requestList).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/request/{requestId}", produces = "application/json", params = "requestId")
    private Response getRequestById (@PathParam("requestId") String requestId) {
        try {
            String resourcePath = String.format("/request/%s", requestId);
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(UUID.fromString(requestId)));
            StateAndRef<RequestState> request = rpcOps.vaultQueryByWithPagingSpec(RequestState.class, queryCriteria, pagingSpec).getStates().get(0);
            return Response.ok(request.getState().getData()).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/request/status", produces = "application/json", params = "status")
    private Response getRequestByStatus (@PathParam("status") String status) {
        try {
            String resourcePath = String.format("/request/status/%s", status);
            RequestState.RequestStateStatus requestStateStatus = RequestState.RequestStateStatus.valueOf(status);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<RequestState>> requests = rpcOps.vaultQueryByCriteria(queryCriteria, RequestState.class).getStates();
            List<RequestState> result = requests.stream().filter(it -> it.getState().getData().getStatus().equals(requestStateStatus)).map(it -> it.getState().getData()).collect(Collectors.toList());
            return Response.ok(result).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/partial/requests", produces = "application/json")
    private Response getAllPartialRequests () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<PartialRequestState>> partialRequestList = rpcOps.vaultQueryByWithPagingSpec(PartialRequestState.class, queryCriteria, pagingSpec).getStates();
            return Response.ok(partialRequestList).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/partial/request/{partialRequestId}", produces = "application/json", params = "partialRequestId")
    private Response getPartialRequestById (@PathParam("partialRequestId") String partialRequestId) {
        try {
            String resourcePath = String.format("/partial/request/%s", partialRequestId);
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(UUID.fromString(partialRequestId)));
            StateAndRef<PartialRequestState> request = rpcOps.vaultQueryByWithPagingSpec(PartialRequestState.class, queryCriteria, pagingSpec).getStates().get(0);
            return Response.ok(request.getState().getData()).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/request", consumes = "application/json", produces = "application/json")
    private Response createRequest (@Valid @RequestBody Request request) {
        try {
            String resourcePath = "/request";

            String authorizedUserUsername = request.getAuthorizedUserUsername();
            String authorizedUserDept = request.getAuthorizedUserDept();
            String externalAccountId = request.getExternalAccountId();
            String purpose = request.getPurpose();
            String amount = request.getAmount();
            String fundStateLinearId = request.getFundStateLinearId();

            BigDecimal amountAndBalance = new BigDecimal(amount);
            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"));
            Currency currency = Currency.getInstance("USD");
            UniqueIdentifier fundStateLinearIdAsUUID = UniqueIdentifier.Companion.fromString(fundStateLinearId);

            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));

            List<AbstractParty> authorizedParties = Arrays.asList(Catan_MoJ);
            List<AbstractParty> participants = Arrays.asList(Catan_MoFA, US_DoS, NewAmerica, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueRequestFlow.InitiatorFlow.class,
                    authorizedUserUsername,
                    authorizedUserDept,
                    externalAccountId,
                    authorizedParties,
                    purpose,
                    amountAndBalance,
                    currency,
                    now,
                    now,
                    fundStateLinearIdAsUUID,
                    participants
            ).getReturnValue().get();
            RequestState created = (RequestState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createRequestSuccessServiceResponse("Request created successfully.", created, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/request", produces = "application/json", params = {"requestId", "authorizerUserUsername"})
    private Response approveRequest (@QueryParam("requestId") String requestId, @QueryParam("authorizerUserUsername") String authorizerUserUsername) {
        try {
            String resourcePath = String.format("/request?requestId=%s?authorizerUserUsername=%s", requestId, authorizerUserUsername);
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    ApproveRequestFlow.InitiatorFlow.class,
                    new UniqueIdentifier(null, UUID.fromString(requestId)),
                    authorizerUserUsername,
                    ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"))
            ).getReturnValue().get();
            RequestState updated = (RequestState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createRequestSuccessServiceResponse("request approved.", updated, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}