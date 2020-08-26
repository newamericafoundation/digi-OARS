package com.newamerica.flows;

import net.corda.core.cordapp.CordappConfig;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.ServiceHub;

public final class CordappConfigUtilities {


    public static final Party getPreferredNotary(ServiceHub services) {

        String notary_config;
        CordappConfig config = services.getAppContext().getConfig();
        notary_config = config.getString("notary");

        CordaX500Name notaryX500Name = CordaX500Name.parse(notary_config);
        Party notary = services.getNetworkMapCache().getNotary(notaryX500Name);
        if (notary != null) {
            return notary;
        } else {
            throw new IllegalStateException("Notary with name " + notaryX500Name + " cannot be found in the network map cache." + "Either the notary does not exist or there is an error in the config.");
        }
    }
}