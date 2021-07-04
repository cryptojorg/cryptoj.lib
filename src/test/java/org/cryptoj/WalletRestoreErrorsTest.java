package org.cryptoj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;

import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Wallet;
import org.cryptoj.utility.FileUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class WalletRestoreErrorsTest extends BaseTest {

	public static final String PASS_PHRASE = "test pass phrase";
	public static final String WALLET_ADDRESS = "14r2TsZ75SHANYaz3WjjYtEfhnVJKcvp3Z";
	
	public static final String WALLET_OK        = "{\"protocol\":\"Bitcoin\", \"version\":\"2.0\", \"wallet\":\"Electrum\", \"account\":{\"address\":\"14r2TsZ75SHANYaz3WjjYtEfhnVJKcvp3Z\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"14r2TsZ75SHANYaz3WjjYtEfhnVJKcvp3Z\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"19g3WzSntjQHnEuVJv7f5iF7zZWWLX5gHo\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1BaLNJQBwfP112tSMEiZuATGN8Nz3mvmrr\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"1AVhsds34bUHY52u4pwWeWvpaTJpEeuXf7\"}]}], \"secret\":\"ttpR/rOUlgyqdyl7CQSxxBVuR5daKM7skVxLDNBPf0EVtaUBplsEJtGsqdyKAeN3v8IvSQr0f6FhvrHOgBm7pDPcCQnZjYYYyBjqIcKnMouK83NIXCvWfpw0QooVbaSA\", \"iv\":\"q9q1O4ADvSN22nQu8nowYg==\"}, \"network\":\"Production\"}";
	public static final String WALLET_TRUNCATED = "{\"protocol\":\"Bitcoin\", \"version\":\"2.0\", \"wallet\":\"Electrum\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/";
	public static final String WALLET_EMPTY = "";
	
	public static final String WALLET_VERSION_MISSING  = "{\"protocol\":\"Bitcoin\", \"wallet\":\"Electrum\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_PROTOCOL_MISSING = "{\"version\":\"2.0\", \"wallet\":\"Electrum\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_WALLET_MISSING   = "{\"protocol\":\"Bitcoin\", \"version\":\"2.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
    public static final String WALLET_NETWORK_MISSING  = "{\"protocol\":\"Bitcoin\", \"version\":\"2.0\", \"wallet\":\"Electrum\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}}";
	public static final String WALLET_ACCOUNT_MISSING  = "{\"protocol\":\"Bitcoin\", \"version\":\"2.0\", \"wallet\":\"Electrum\", \"network\":\"Production\"}";

	@BeforeAll
	public static void logOutput() {
		logStatic("--- running tests in WalletRestoreErrorsTest ---", false);
	}
	
	@Test
	public void happyCaseTest() throws IOException {
		Wallet wallet = restoreFromContent(WALLET_OK);
		assertNotNull(wallet);
		assertEquals(WALLET_ADDRESS, wallet.getAccount().getAddress());
	}

	@Test
	public void truncatedFileTest() throws IOException {
		VerifyWalletFileException e = assertThrows(VerifyWalletFileException.class, () -> { restoreFromContent(WALLET_TRUNCATED); });
		log("truncatedFileTest(): " + e.getMessage());		
	}

	@Test
	public void emtpyFileTest() throws IOException {
		VerifyWalletFileException e = assertThrows(VerifyWalletFileException.class, () -> { restoreFromContent(null); });
		log("emtpyFileTest(): " + e.getMessage());		
	}

	@Test
	public void versionMissingTest() throws IOException {
		runMissingSomething(WALLET_VERSION_MISSING, "versionMissingTest"); 
	}

	@Test
	public void protocolMissingTest() throws IOException {
		runMissingSomething(WALLET_PROTOCOL_MISSING, "protocolMissingTest"); 
	}

	@Test
	public void walletlMissingTest() throws IOException {
		runMissingSomething(WALLET_WALLET_MISSING, "walletlMissingTest"); 
	}

	@Test
	public void networkMissingTest() throws IOException {
		runMissingSomething(WALLET_NETWORK_MISSING, "networkMissingTest"); 
	}

	@Test
	public void accountlMissingTest() throws IOException {
		runMissingSomething(WALLET_ACCOUNT_MISSING, "accountlMissingTest"); 
	}
	
	private void runMissingSomething(String wallet_text, String methodName) {
		VerifyWalletFileException e = assertThrows(VerifyWalletFileException.class, () -> { 
			restoreFromContent(wallet_text); 
		});
		
		log(methodName + "(): " + e.getMessage() + " " + e.getCause().getCause());		
	}
	
	private Wallet restoreFromContent(String content) throws IOException {
		File wallet = createTempFile();
		boolean overwrite = true;
		FileUtility.saveToFile(content, wallet.getAbsolutePath(), overwrite);
		
		String [] args = {
				Application.SWITCH_PASS_PHRASE, PASS_PHRASE, 
				Application.SWITCH_VERIFY, wallet.getAbsolutePath()
		};
		
		Application app = new Application();
		app.processCommandLine(args);
		
		return app.restoreWallet(wallet);
	}
}
