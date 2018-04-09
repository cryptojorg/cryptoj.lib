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
	public static final String WALLET_ADDRESS = "1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg";
	
	public static final String WALLET_OK        = "{\"technology\":\"Bitcoin\", \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_TRUNCATED = "{\"technology\":\"Bitcoin\", \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/";
	public static final String WALLET_EMPTY = "";
	
	public static final String WALLET_VERSION_MISSING = "{\"technology\":\"Bitcoin\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_TECH_MISSING = "{\"technology\":\"Bitcoin\", \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_NETWORK_MISSING = "{\"technology\":\"Bitcoin\", \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_ACCOUNT_MISSING = "{\"technology\":\"Bitcoin\", \"version\":\"1.0\", \"network\":\"Production\"}";
	
	public static final String WALLET_CORRUPT_1 = "{\"technology\":, \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_CORRUPT_2 = "{\"technology\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";
	public static final String WALLET_CORRUPT_3 = "{\"Bitcoin\", \"version\":\"1.0\", \"account\":{\"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\", \"encrypted\":true, \"chains\":[{\"path\":\"M/44H/0H/0H/0\", \"addresses\":[{\"path\":\"M/44H/0H/0H/0/0\", \"address\":\"1CgtUEBjUwNsMG6n7wzzugGkykA4iYqJCg\"},{\"path\":\"M/44H/0H/0H/0/1\", \"address\":\"14oCjjSGaUehKkTtMaRspSX9EXTt2h6qdY\"}]},{\"path\":\"M/44H/0H/0H/1\", \"addresses\":[{\"path\":\"M/44H/0H/0H/1/0\", \"address\":\"1JKkouoYNLzwY4jQopdcWQMj9U2afWDvM2\"},{\"path\":\"M/44H/0H/0H/1/1\", \"address\":\"15AijYQjGWvH8GSqzj7Q81aNMs7b9neCbi\"}]}], \"secret\":\"43gGRnjsSYM86k9BVBiaOkMFb2qXl5WljdPhzPc7308Z4J1TQ5sNCauCWxRUqahQnur5hijmiygh+/ttJedFvXJzqbbV5VYEYL1IvpX4E0c=\", \"iv\":\"3S7Tda1bR2MZ7WWfUCUu8w==\"}, \"network\":\"Production\"}";

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
		VerifyWalletFileException e = assertThrows(VerifyWalletFileException.class, () -> { restoreFromContent(WALLET_VERSION_MISSING); });
		// TODO there must be a better way ... inside verifywalletfileexception is illegal...
		log("versionMissingTest(): " + e.getMessage() + " " + e.getCause().getCause());		
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
