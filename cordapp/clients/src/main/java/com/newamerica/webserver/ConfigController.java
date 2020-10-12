package com.newamerica.webserver;

import com.newamerica.flows.IssueConfigFlow;
import com.newamerica.flows.IssueFundFlow;
import com.newamerica.states.ConfigState;
import com.newamerica.states.FundState;
import com.newamerica.webserver.dtos.Config;
import com.newamerica.webserver.dtos.Fund;
import net.corda.core.contracts.StateAndRef;
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
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class ConfigController extends BaseResource {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(ConfigController.class);
    public ConfigController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @PostMapping(value = "/config", consumes = "application/json", produces = "application/json")
    private Response createConfig (@Valid @RequestBody Config config) {
        try {
            String resourcePath = "/config";
            String maxWithdrawalAmountStr = config.getMaxWithdrawalAmount();

            Party US_DoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoJ,L=New York,C=US"));
            Party US_DoS = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=US_DoS,L=New York,C=US"));
            Party NewAmerica = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=NewAmerica,L=New York,C=US"));
            Party Catan_MoJ = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoJ,L=London,C=GB"));
            Party Catan_MoFA = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_MoFA,L=London,C=GB"));
            Party Catan_Treasury = rpcOps.wellKnownPartyFromX500Name(CordaX500Name.parse("O=Catan_Treasury,L=London,C=GB"));

            ZonedDateTime now = ZonedDateTime.ofInstant(Instant.from(ZonedDateTime.now()), ZoneId.of("UTC"));
            BigDecimal maxWithdrawalAmount = new BigDecimal(maxWithdrawalAmountStr);
            Currency currency = Currency.getInstance("USD");

            List<AbstractParty> participants = Arrays.asList(US_DoJ, US_DoS, NewAmerica, Catan_MoFA, Catan_MoJ, Catan_Treasury);

            SignedTransaction tx = rpcOps.startFlowDynamic(
                    IssueConfigFlow.InitiatorFlow.class,
                    config.getCreator(),
                    config.getCountry(),
                    maxWithdrawalAmount,
                    currency,
                    now,
                    participants
            ).getReturnValue().get();
            ConfigState created = (ConfigState) tx.getTx().getOutputs().get(0).getData();
            return Response.ok(createConfigSuccessServiceResponse("Config created successfully.", created, resourcePath)).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        }catch (Exception e) {
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = "/config", produces = "application/json")
    private Response getLatestConfig () {
        try {
            QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, null, null, Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<ConfigState>> configs = rpcOps.vaultQueryByCriteria(queryCriteria, ConfigState.class).getStates();
            ConfigState result = configs.stream().map(it -> it.getState().getData()).sorted(Comparator.comparing(ConfigState::getCreateDatetime).reversed()).collect(Collectors.toList()).get(0);
            return Response.ok(result).build();
        }catch (IllegalArgumentException e) {
            return customizeErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return customizeErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
