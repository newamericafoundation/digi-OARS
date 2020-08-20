package com.newamerica;

import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;

public class TestUtils {
    public static TestIdentity US = new TestIdentity(new CordaX500Name("United States", "TestLand", "US"));
    public static TestIdentity CATAN = new TestIdentity(new CordaX500Name("Catan", "TestCity", "BG"));
}
