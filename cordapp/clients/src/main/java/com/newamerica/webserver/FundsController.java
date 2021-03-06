package com.newamerica.webserver;

import com.newamerica.flows.IssueFundFlow;
import com.newamerica.flows.ReceiveFundFlow;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import com.newamerica.webserver.dtos.Fund;
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
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_NUM;

/**
 * Define your API endpoints here.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class FundsController extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(FundsController.class);

    public FundsController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @GetMapping(value = "/funds", produces = "application/json")
    private Response getAllUnconsumedFunds () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<FundState>> fundList = rpcOps.vaultQueryByWithPagingSpec(FundState.class, queryCriteria, pagingSpec).getStates();
            List<FundState> resultSet = fundList.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(FundState::getCreateDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/all-funds", produces = "application/json")
    private Response getAllFunds () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.ALL);
            List<StateAndRef<FundState>> fundList = rpcOps.vaultQueryByWithPagingSpec(FundState.class, queryCriteria, pagingSpec).getStates();
            List<FundState> resultSet = fundList.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(FundState::getCreateDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/fund/{fundId}", produces = "application/json", params = "fundId")
    private Response getFundById (@PathParam("fundId") String fundId) {
        try {
            String resourcePath = String.format("/fund/%s", fundId);
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UUID.fromString(fundId)), null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<FundState>> requestList = rpcOps.vaultQueryByCriteria(queryCriteria, FundState.class).getStates();
            List<FundState> resultSet =
                    requestList.stream()
                            .map(it -> it.getState().getData())
                            .sorted(Comparator.comparing(FundState::getUpdateDatetime).reversed())
                            .collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    @GetMapping(value = "/fund/all", produces = "application/json", params = "fundId")
    private Response getFundByIdAll (@QueryParam("fundId") String fundId) {
        try {
            String resourcePath = String.format("/fund/all", fundId);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Collections.singletonList(UUID.fromString(fundId)), null, Vault.StateStatus.ALL);
            List<StateAndRef<FundState>> requestList = rpcOps.vaultQueryByCriteria(queryCriteria, FundState.class).getStates();
            List<FundState> resultSet =
                    requestList.stream()
                            .map(it -> it.getState().getData())
                            .sorted((comparing(FundState::getUpdateDatetime).reversed()).thenComparing(FundState::getBalance))
                            .collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/fund/aggregate", produces = "application/json", params = {"startDate", "endDate"})
    private Response getFundAggregate (@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
        try {
            String resourcePath = String.format("/fund?startDate=%s?endDate=%s", startDate, endDate);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.ALL);
            List<StateAndRef<FundState>> funds = rpcOps.vaultQueryByCriteria(queryCriteria, FundState.class).getStates();
            List<FundState> resultSet = funds.stream().filter(it -> it.getState().getData().getStatus().equals(FundState.FundStateStatus.ISSUED)).map(it -> it.getState().getData()).collect(Collectors.toList());

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

                //check to see if the current fundstate is within the specified range
                if(startDateTime.compareTo(resultSet.get(i).getCreateDatetime()) <= 0 && endDateTime.compareTo(resultSet.get(i).getCreateDatetime()) >= 0){

                    //if this date aggregate hasn't been seen yet, add it to the aggregateMap
                    if(!aggregateMap.containsKey(currentDateMonth + "-" + currentDateDay + "-" + currentDateYear)){

                        //add a key as the fundstate date (MM-dd-YYYY to aggregateMap
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

    @GetMapping(value = "/fund/status", produces = "application/json", params = "status")
    private Response getFundByStatus (@PathParam("status") String status) {
        try {
            String resourcePath = String.format("/fund/status/%s", status);
            FundState.FundStateStatus fundStateStatus = FundState.FundStateStatus.valueOf(status);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
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

    @PostMapping(value = "/fund", consumes = "application/json", produces = "application/json")
    private Response createFund (@Valid @RequestBody Fund request) {
        try {
            String resourcePath = "/fund";

            String originPartyName = request.getOriginParty();
            String receivingPartyName = request.getReceivingParty();
            String amountStr = request.getAmount();
            String accountId = request.getAccountId();

            Party originParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(originPartyName));
            Party receivingParty = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse(receivingPartyName));
            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));
            Party Catan_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_CSO,L=London,C=GB"));
            Party US_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_CSO,L=New York,C=US"));

            BigDecimal amountAndBalance = new BigDecimal(amountStr);
            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"));
            Currency currency = Currency.getInstance("USD");

            List<AbstractParty> owners = Arrays.asList(originParty);
            List<AbstractParty> requiredSigners =  Arrays.asList(Catan_MoJ);
            List<AbstractParty> partialRequestParticipants = Arrays.asList(Catan_CSO, US_CSO);
            List<AbstractParty> participants = Arrays.asList(originParty, US_DoS, NewAmerica, Catan_MoFA, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueFundFlow.InitiatorFlow.class,
                    originParty,
                    receivingParty,
                    accountId,
                    owners,
                    requiredSigners,
                    partialRequestParticipants,
                    amountAndBalance,
                    now,
                    now,
                    currency,
                    participants
            ).getReturnValue().get();
            FundState created = (FundState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createFundSuccessServiceResponse("Fund created successfully.", created, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/fund", produces = "application/json", params = "fundId")
    private Response receiveFund (@QueryParam("fundId") String fundId, @QueryParam("receivedByUsername") String receivedByUsername) {
        try {
            String resourcePath = String.format("/fund?fundId=%s", fundId);
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    ReceiveFundFlow.InitiatorFlow.class,
                    receivedByUsername,
                    new UniqueIdentifier(null, UUID.fromString(fundId)),
                    ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"))
            ).getReturnValue().get();
            FundState updated = (FundState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createFundSuccessServiceResponse("Fund received successfully.", updated, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}