package org.cryptoj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.cryptoj.coin.bitcoin.BitcoinAccount;
import org.cryptoj.coin.ethereum.EthereumAccount;
import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.ProtocolEnum;
import org.cryptoj.core.Wallet;
import org.junit.jupiter.api.Test;

public class WalletCreateRestoreTest extends BaseTest {

	public static final String PASS_PHRASE = "test pass phrase";

	/**
	 * Test ethereum production wallet
	 */
	@Test
	public void createEthereumProductionWalletTest() {
		Protocol expectedProtocol = ProtocolFactory.getInstance(ProtocolEnum.Ethereum, Network.Production);
		String [] args = {
				Application.SWITCH_PASS_PHRASE, PASS_PHRASE, 
				Application.SWITCH_PROTOCOL, "ethereum", 
				Application.SWITCH_NETWORK, "production",
				Application.SWITCH_WALLET, EthereumAccount.WALLET_METAMASK
		};
		
		createAndRestore(args, expectedProtocol);
	}

	/**
	 * Test ethereum test wallet
	 */
	@Test
	public void createEthereumTestWalletTest() {
		Protocol expectedProtocol = ProtocolFactory.getInstance(ProtocolEnum.Ethereum, Network.Test);
		String [] args = {
				Application.SWITCH_PASS_PHRASE, PASS_PHRASE, 
				Application.SWITCH_PROTOCOL, "ethereum", 
				Application.SWITCH_NETWORK, "test",
				Application.SWITCH_WALLET, EthereumAccount.WALLET_METAMASK
		};
		
		createAndRestore(args, expectedProtocol);
	}
	
	/**
	 * Test bitcoin production wallet
	 */
	@Test
	public void createBitcoinProductionWalletTest() {
		Protocol expectedProtocol = ProtocolFactory.getInstance(ProtocolEnum.Bitcoin, Network.Production);
		String [] args = {
				Application.SWITCH_PASS_PHRASE, PASS_PHRASE, 
				Application.SWITCH_PROTOCOL, "bitcoin", 
				Application.SWITCH_NETWORK, "production",
				Application.SWITCH_WALLET, BitcoinAccount.WALLET_ELECTRUM
		};
		
		createAndRestore(args, expectedProtocol);
	}

	/**
	 * Test bitcoin test wallet
	 */
	@Test
	public void createBitcoinTestWalletTest() {
		Protocol expectedProtocol = ProtocolFactory.getInstance(ProtocolEnum.Bitcoin, Network.Test);
		String [] args = {
				Application.SWITCH_PASS_PHRASE, PASS_PHRASE, 
				Application.SWITCH_PROTOCOL, "bitcoin", 
				Application.SWITCH_NETWORK, "test",
				Application.SWITCH_WALLET, BitcoinAccount.WALLET_ELECTRUM
		};
		
		createAndRestore(args, expectedProtocol);
	}

	private void createAndRestore(String [] args, Protocol expectedProtocol) {
		Application app = new Application();
		app.processCommandLine(args);
		Wallet wallet = app.createWallet();
		assertNotNull(wallet);

		assertEquals(expectedProtocol, wallet.getProtocol());

		// write wallet to disk
		String directory = app.getDirectory();
		File file = app.writeWalletFile(wallet, directory);
		file.deleteOnExit();

		// verify file exists and mark to delete
		assertNotNull(file);
		assertTrue(file.exists());

		// create a 2nd wallet restored from the file
		Wallet walletRestored = app.restoreWallet(file);

		// make sure the restored wallet matches the original
		assertNotNull(walletRestored);
		assertEquals(wallet, walletRestored);
	}
}
