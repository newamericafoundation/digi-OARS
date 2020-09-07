package com.newamerica.webserver;

import com.newamerica.flows.IssueRequestFlow;
import com.newamerica.states.RequestState;
import com.newamerica.webserver.dtos.Request;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping(value = "/request", consumes = "application/json", produces = "application/json")
    private Response createRequest (@Valid @RequestBody Request request) {
        try {
            String resourcePath = "/request";

            String authorizedUserUsername = request.getAuthorizedUserUsername();
            String authorizedUserDept = request.getAuthorizedUserDept();
            String authorizerUserUsername = request.getAuthorizerUserUsername();
            String externalAccountId = request.getExternalAccountId();
            String amount = request.getAmount();
            String fundStateLinearId = request.getFundStateLinearId();

            BigDecimal amountAndBalance = new BigDecimal(amount);
            ZonedDateTime now = ZonedDateTime.now();
            Currency currency = Currency.getInstance("USD");
            UniqueIdentifier fundStateLinearIdAsUUID = UniqueIdentifier.Companion.fromString(fundStateLinearId);

            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));
            Party Catan_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_CSO,L=London,C=GB"));
            Party US_CSO = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_CSO,L=New York,C=US"));

            List<AbstractParty> participants = Arrays.asList(Catan_MoFA, US_DoS, NewAmerica, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueRequestFlow.InitiatorFlow.class,
                    authorizedUserUsername,
                    authorizedUserDept,
                    authorizerUserUsername,
                    externalAccountId,
                    amountAndBalance,
                    currency,
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
}