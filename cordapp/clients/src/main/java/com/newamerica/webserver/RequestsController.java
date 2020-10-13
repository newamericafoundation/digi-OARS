package com.newamerica.webserver;

import com.newamerica.flows.ApproveRequestFlow;
import com.newamerica.flows.IssueRequestFlow;
import com.newamerica.flows.RejectRequestFlow;
import com.newamerica.states.FundState;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
            List<RequestState> resultSet = requestList.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(RequestState::getCreateDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/requests", produces = "application/json", params = "fundId")
    private Response getRequestsByFundId (@QueryParam("fundId") String fundId) {
        try {
            String resourcePath = "/requests";
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<RequestState>> requestStates = rpcOps.vaultQueryByWithPagingSpec(RequestState.class, queryCriteria, pagingSpec).getStates();
            List<RequestState> resultSet = requestStates.stream().map(it -> it.getState().getData()).filter(it -> it.getFundStateLinearId().getId().equals(UUID.fromString(fundId))).sorted(Comparator.comparing(RequestState::getCreateDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
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
            List<PartialRequestState> resultSet = partialRequestList.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(PartialRequestState::getDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
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

            BigDecimal amountAndBalance = new BigDecimal(amount);
            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"));
            Currency currency = Currency.getInstance("USD");

            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party US_DoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoJ,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));

            List<AbstractParty> participants = Arrays.asList(Catan_MoFA,US_DoJ, US_DoS, NewAmerica, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueRequestFlow.InitiatorFlow.class,
                    authorizedUserUsername,
                    authorizedUserDept,
                    externalAccountId,
                    purpose,
                    amountAndBalance,
                    currency,
                    now,
                    now,
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

    @PutMapping(value = "/request/approve", produces = "application/json", params = {"requestStateLinearId", "authorizerUserUsername", "authorizerUserDept", "fundStateLinearId"})
    private Response approveRequest (@QueryParam("requestStateLinearId") String requestStateLinearId,
                                     @QueryParam("authorizerUserUsername") String authorizerUserUsername,
                                     @QueryParam("authorizerUserDept") String authorizerUserDept,
                                     @QueryParam("fundStateLinearId") String fundStateLinearId) {
        try {
            String resourcePath = String.format("/request?requestStateLinearId=%s?authorizerUserUsername=%s?authorizerUserDept=%s", requestStateLinearId, authorizerUserUsername, authorizerUserDept);
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    ApproveRequestFlow.InitiatorFlow.class,
                    new UniqueIdentifier(null, UUID.fromString(requestStateLinearId)),
                    authorizerUserUsername,
                    authorizerUserDept,
                    ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC")),
                    new UniqueIdentifier(null, UUID.fromString(fundStateLinearId))
            ).getReturnValue().get();
            RequestState updated = (RequestState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createRequestSuccessServiceResponse("Request approved.", updated, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/request/reject", produces = "application/json", params = {"requestStateLinearId", "authorizerUserUsername", "authorizerUserDept"})
    private Response rejectRequest (@QueryParam("requestStateLinearId") String requestStateLinearId, @QueryParam("authorizerUserUsername") String authorizerUserUsername, @QueryParam("authorizerUserDept") String authorizerUserDept) {
        try {
            String resourcePath = String.format("/request?requestStateLinearId=%s?authorizerUserUsername=%s?authorizerUserDept=%s", requestStateLinearId, authorizerUserUsername, authorizerUserDept);
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    RejectRequestFlow.InitiatorFlow.class,
                    new UniqueIdentifier(null, UUID.fromString(requestStateLinearId)),
                    authorizerUserUsername,
                    authorizerUserDept,
                    ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"))
            ).getReturnValue().get();
            RequestState updated = (RequestState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createRequestSuccessServiceResponse("Request rejected.", updated, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/request/aggregate", produces = "application/json", params = {"startDate", "endDate", "department", "status"})
    private Response getFundAggregate (@PathParam("startDate") String startDate, @PathParam("endDate") String endDate, @PathParam("department") String department, @PathParam("status") String status) throws ParseException {
        try {
            String resourcePath = String.format("/fund?startDate=%s?endDate=%s?department=%s?status=%s", startDate, endDate, department, status);
            RequestState.RequestStateStatus requestStateStatus = RequestState.RequestStateStatus.valueOf(status);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.ALL);
            List<StateAndRef<RequestState>> funds = rpcOps.vaultQueryByCriteria(queryCriteria, RequestState.class).getStates();
            List<RequestState> resultSet = funds.stream().filter(it -> it.getState().getData().getStatus().equals(requestStateStatus) && it.getState().getData().getAuthorizedUserDept().equalsIgnoreCase(department)).map(it -> it.getState().getData()).collect(Collectors.toList());

            // parse the start and end dates
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            Date startDateFormatted = format.parse(startDate);
            Date endDateFormatted = format.parse(endDate);
            ZonedDateTime startDateTime = startDateFormatted.toInstant().atZone(ZoneId.of("UTC"));
            ZonedDateTime endDateTime = endDateFormatted.toInstant().atZone(ZoneId.of("UTC"));

            //map used to store all date aggregates and their respective attributes
            Map<String, Map<String, BigDecimal>> aggregateMap = new HashMap<>();

            for(int i = 0; i < resultSet.size(); i++){

                int currentDateDay = resultSet.get(i).getCreateDatetime().getDayOfMonth();
                int currentDateMonth = resultSet.get(i).getCreateDatetime().getMonthValue();
                int currentDateYear = resultSet.get(i).getCreateDatetime().getYear();

                //check to see if the current requestState is within the specified range
                if(startDateTime.compareTo(resultSet.get(i).getCreateDatetime()) <= 0 && endDateTime.compareTo(resultSet.get(i).getCreateDatetime()) >= 0){

                    //if this date aggregate hasn't been seen yet, add it to the aggregateMap
                    if(!aggregateMap.containsKey(currentDateMonth + "-" + currentDateDay + "-" + currentDateYear)){

                        //add a key as the requestState date (MM-dd-YYYY to aggregateMap
                        // with a value of a new attribute map of a count and total amount
                        Map<String, BigDecimal> currentDateAttributes = new HashMap<>();
                        currentDateAttributes.put("count", BigDecimal.ONE);
                        currentDateAttributes.put("totalAmount", resultSet.get(i).getAmount());
                        aggregateMap.put(currentDateMonth + "-" + currentDateDay + "-" + currentDateYear, currentDateAttributes);
                    }
                    // else if it does exist already, increment the count and add to the sum of total amount
                    else{
                        Map<String, BigDecimal> existingDateAttributes = aggregateMap.get(currentDateMonth + "-" + currentDateDay + "-" + currentDateYear);
                        existingDateAttributes.put("count", existingDateAttributes.get("count").add(BigDecimal.ONE));
                        existingDateAttributes.put("totalAmount", existingDateAttributes.get("totalAmount").add(resultSet.get(i).getAmount()));
                        aggregateMap.put(currentDateMonth + "-" + currentDateDay + "-" + currentDateYear, existingDateAttributes);
                    }
                }

            }

            return Response.ok(aggregateMap).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}