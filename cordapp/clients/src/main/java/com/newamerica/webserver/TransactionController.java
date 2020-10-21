package com.newamerica.webserver;

import com.newamerica.webserver.responses.TransactionList;
import com.newamerica.webserver.service.TransactionService;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.CoreTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class TransactionController {
    private final CordaRPCOps rpcOps;
    private final static Logger logger = LoggerFactory.getLogger(RequestsController.class);

    public TransactionController(NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
    }

    @GetMapping(value = "/transactions", produces = "application/json")
    @SuppressWarnings( "deprecation" )
    private Response getAllTransactions() {
        TransactionService txService = new TransactionService(rpcOps);
        List<SignedTransaction> signedTransactions = rpcOps.internalVerifiedTransactionsSnapshot();
        TransactionList transactionList = new TransactionList();
        List<TransactionList.TransactionData> transactionDataList = new ArrayList<>();
        transactionList.setTotalRecords(signedTransactions.size());
        int initial = signedTransactions.size() - 1;

        for (int i = initial; i >= 0; i--) {
            try {
                CoreTransaction coreTransaction = signedTransactions.get(i).getCoreTransaction();
                TransactionList.TransactionData transactionData = new TransactionList.TransactionData();

                    transactionData.setSigners(txService.getSignersFromTx(signedTransactions.get(i)));

                    if (coreTransaction.getInputs().size() > 0) {
                        transactionData.setInputs(txService.getInputsFromTx(coreTransaction));
                        txService.addInputTypeAndCount(transactionData);
                    }

                    transactionData.setOutputs(txService.getOutputsFromTx(coreTransaction));
                    transactionData.setOutputTypes(txService.getOutputTypeAndCount(coreTransaction));
                transactionData.setTransactionId(coreTransaction.getId().toString());
                transactionData.setCommands(((WireTransaction) coreTransaction).getCommands().stream().map(command ->
                        command.getValue().getClass().getCanonicalName().substring(command.getValue().getClass()
                                .getCanonicalName().lastIndexOf(".") + 1)).collect(Collectors.toList()));

                transactionData.setNotary(coreTransaction.getNotary().getName().getOrganisation());
                transactionDataList.add(transactionData);
                transactionList.setTransactionData(transactionDataList);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return Response.ok(transactionList).build();
    }
}
