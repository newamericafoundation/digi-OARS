package com.newamerica.webserver;

import com.newamerica.flows.IssueFundFlow;
import com.newamerica.flows.ReceiveFundFlow;
import com.newamerica.states.FundState;
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
            return Response.ok(fundList).build();
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
            return Response.ok(fundList).build();
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

    @GetMapping(value = "/fund/status/{status}", produces = "application/json", params = "status")
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
            String maxWithdrawalAmountStr = request.getMaxWithdrawalAmount();

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
                    now,
                    maxWithdrawalAmount,
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
    private Response receiveFund (@QueryParam("fundId") String fundId) {
        try {
            String resourcePath = String.format("/fund?fundId=%s", fundId);
            SignedTransaction tx = rpcOps.startFlowDynamic(
                    ReceiveFundFlow.InitiatorFlow.class,
                    new UniqueIdentifier(null, UUID.fromString(fundId)),
                    ZonedDateTime.now()
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