package com.newamerica;

import net.corda.core.identity.CordaX500Name;
import net.corda.testing.core.TestIdentity;

public class TestUtils {
    public static TestIdentity US = new TestIdentity(new CordaX500Name("United States", "TestLand", "US"));
    public static TestIdentity US_CSO = new TestIdentity(new CordaX500Name("US CSO", "TestLand", "US"));
    public static TestIdentity US_DoJ = new TestIdentity(new CordaX500Name("US DoJ", "TestLand", "US"));
    public static TestIdentity US_DoS = new TestIdentity(new CordaX500Name("US DoS", "TestLand", "US"));
    public static TestIdentity NewAmerica = new TestIdentity(new CordaX500Name("New America", "TestLand", "US"));
    public static TestIdentity CATAN = new TestIdentity(new CordaX500Name("Catan CSO", "TestCity", "BG"));
    public static TestIdentity CATAN_CSO = new TestIdentity(new CordaX500Name("Catan", "TestCity", "BG"));
    public static TestIdentity CATANMoJ = new TestIdentity(new CordaX500Name("CatanMoJ", "TestCity", "BG"));
    public static TestIdentity CATANMoFA = new TestIdentity(new CordaX500Name("CatanMoFA", "TestCity", "BG"));
    public static TestIdentity CATANTreasury = new TestIdentity(new CordaX500Name("CatanTreasury", "TestCity", "BG"));
}
