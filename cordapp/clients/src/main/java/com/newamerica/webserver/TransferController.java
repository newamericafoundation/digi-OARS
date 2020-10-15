package com.newamerica.webserver;

import com.newamerica.flows.IssueTransferFlow;
import com.newamerica.states.FundState;
import com.newamerica.states.RequestState;
import com.newamerica.states.TransferState;
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

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.corda.core.node.services.vault.QueryCriteriaUtils.DEFAULT_PAGE_NUM;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TransferController extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(TransferController.class);

    public TransferController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }


    @GetMapping(value = "/transfers", produces = "application/json")
    private Response getAllTransfers () {
        try {
            PageSpecification pagingSpec = new PageSpecification(DEFAULT_PAGE_NUM, 100);
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<TransferState>> transferList = rpcOps.vaultQueryByWithPagingSpec(TransferState.class, queryCriteria, pagingSpec).getStates();
            List<TransferState> resultSet = transferList.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(TransferState::getDatetime).reversed()).collect(Collectors.toList());
            return Response.ok(resultSet).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = "/transfer", produces = "application/json", params = "requestId")
    private Response createTransfer (@QueryParam("requestId") String requestId) {
        try {
            String resourcePath = String.format("/request?requestId=%s", requestId);

            UniqueIdentifier requestStateLinearIdAsUUID = UniqueIdentifier.Companion.fromString(requestId);
            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party US_DoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoJ,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));

            List<AbstractParty> participants = Arrays.asList(Catan_MoFA, US_DoS, US_DoJ, NewAmerica, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueTransferFlow.InitiatorFlow.class,
                    requestStateLinearIdAsUUID,
                    participants
            ).getReturnValue().get();
            TransferState created = (TransferState) tx.getTx().getOutputs().get(0).getData();
            UUID requestUUID = created.getRequestStateLinearId().getId();
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, Arrays.asList(requestUUID));
            StateAndRef<RequestState> req = rpcOps.vaultQueryByCriteria(queryCriteria,RequestState.class).getStates().get(0);
            return Response.ok(req.getState().getData()).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }




}
