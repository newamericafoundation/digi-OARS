package com.newamerica.webserver.service;

import com.newamerica.webserver.responses.TransactionList;
import net.corda.core.contracts.StateRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.transactions.CoreTransaction;
import net.corda.core.transactions.SignedTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionService {
    private final CordaRPCOps rpcOps;

    public TransactionService(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
    }

    public List<TransactionList.Signer> getSignersFromTx(SignedTransaction signedTransaction){
        List<TransactionList.Signer> signerList = new ArrayList<>();
        signedTransaction.getSigs().forEach(signature -> {
            TransactionList.Signer signer = new TransactionList.Signer();
            signer.setSignature(signature);
            signer.setPartyName(rpcOps.partyFromKey(signature.getBy()).nameOrNull().toString());
            signerList.add(signer);
        });
        return signerList;
    }

    @SuppressWarnings( "deprecation" )
    public List<TransactionList.StateAndType> getInputsFromTx(CoreTransaction coreTransaction){
        List<TransactionList.StateAndType> inputList = new ArrayList<>();
        for (StateRef stateRef : coreTransaction.getInputs()) {
            SignedTransaction signedTransaction = rpcOps
                    .internalFindVerifiedTransaction(stateRef.getTxhash());
            if (signedTransaction != null) {
                inputList.add(new TransactionList.StateAndType(
                        signedTransaction.getCoreTransaction().getOutputStates().get(stateRef.getIndex()),
                        signedTransaction.getCoreTransaction().getOutputStates().get(stateRef.getIndex())
                                .getClass().getCanonicalName(), stateRef));
            }
        }
        return inputList;
    }

    public void addInputTypeAndCount(TransactionList.TransactionData transactionData){
        Map<String, Integer> inputTypeMap = new HashMap<>();
        transactionData.getInputs().forEach(stateAndType -> {
            String type = stateAndType.getState().getClass().toString().substring(
                    stateAndType.getState().getClass().toString().lastIndexOf(".") + 1);
            if (inputTypeMap.containsKey(type)) {
                inputTypeMap.put(type, inputTypeMap.get(type) + 1);
            } else {
                inputTypeMap.put(type, 1);
            }
        });

        List<TransactionList.TypeCount> inputTypeCountList = new ArrayList<>();
        inputTypeMap.keySet().forEach(s -> {
            TransactionList.TypeCount typeCount = new TransactionList.TypeCount();
            typeCount.setType(s);
            typeCount.setCount(inputTypeMap.get(s));
            inputTypeCountList.add(typeCount);
        });

        transactionData.setInputTypes(inputTypeCountList);
    }

    public List<TransactionList.StateAndType> getOutputsFromTx(CoreTransaction coreTransaction){
        List<TransactionList.StateAndType> outputList = new ArrayList<>();
        AtomicReference<Integer> counter = new AtomicReference<>(0);
        coreTransaction.getOutputStates().forEach(contractState -> {
            outputList.add(new TransactionList.StateAndType(
                    contractState,
                    contractState.getClass().getCanonicalName(),
                    new StateRef(coreTransaction.getId(), counter.get()))
            );
            counter.getAndSet(counter.get() + 1);
        });
        return outputList;
    }

    public List<TransactionList.TypeCount> getOutputTypeAndCount(CoreTransaction coreTransaction){
        Map<String, Integer> outputTypeMap = new HashMap<>();
        coreTransaction.getOutputStates().forEach(contractState -> {
            String type = contractState.getClass().toString().substring(
                    contractState.getClass().toString().lastIndexOf(".") + 1);
            if (outputTypeMap.containsKey(type)) {
                outputTypeMap.put(type, outputTypeMap.get(type) + 1);
            } else {
                outputTypeMap.put(type, 1);
            }
        });
        List<TransactionList.TypeCount> outputTypeCountList = new ArrayList<>();
        outputTypeMap.keySet().forEach(s -> {
            TransactionList.TypeCount typeCount = new TransactionList.TypeCount();
            typeCount.setType(s);
            typeCount.setCount(outputTypeMap.get(s));
            outputTypeCountList.add(typeCount);
        });
        return outputTypeCountList;
    }
}
