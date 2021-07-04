package org.cryptoj.ethereum;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.cryptoj.coin.ethereum.Ethereum;
import org.cryptoj.coin.ethereum.EthereumAccount;
import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolEnum;
import org.cryptoj.core.ProtocolFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class AccountBaseTest extends BaseTest {
	
	public static final String MNEMONIC_WORDS_FIXED = "expose dwarf coyote broken alert rifle fade novel estate output about repair";
	public static final String ADDRESS_FIXED = "0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f";
	public static final String PASS_PHRASE = "test pass phrase";
	public static final String SECRET_FIXED = MNEMONIC_WORDS_FIXED + "#" + PASS_PHRASE;
	
	@Test
	public void verifyMatchingAddress() throws IOException, JSONException {
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS_FIXED);
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(ProtocolEnum.Ethereum, network);
		String targetWallet = EthereumAccount.WALLET_METAMASK;
		
		Account account = protocol.createAccount(mnemonicWords, PASS_PHRASE, network, targetWallet);
		String secret = account.deriveSecret(mnemonicWords);
		String address = account.deriveAddress(secret);
		
		assertNotNull(account);
		assertNotNull(secret);
		assertNotNull(address);
		
		assertEquals(ADDRESS_FIXED, address, "Address mismatch");
		assertEquals(SECRET_FIXED, secret, "Secret mismatch");
		
		assertEquals(secret, account.getSecret());
		assertEquals(address, account.getAddress());
	}

	@Test
	public void testCreateAndRestore() throws IOException, JSONException {
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(ProtocolEnum.Ethereum, network);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		String targetWallet = EthereumAccount.WALLET_METAMASK;
		Account accountNew = protocol.createAccount(mnemonicWords, PASS_PHRASE, network, targetWallet);

		assertNotNull(accountNew);
		
		JSONObject json = accountNew.toJson(false);
		Account accountRestored = protocol.restoreAccount(json, PASS_PHRASE, targetWallet);
		
		assertEquals(accountNew, accountRestored);
	}

	@Test
	public void testCreateAccount() throws IOException {
		log("--- start testCreateAccount() ---");

		Protocol protocol = new Ethereum(Network.Production);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Network network = protocol.getNetwork();
		String targetWallet = EthereumAccount.WALLET_METAMASK;
		
		Account account = new EthereumAccount(mnemonicWords, PASS_PHRASE, network, targetWallet);

		log("mnemonic words: '%s'", String.join(" ", mnemonicWords));

		assertNotNull(account);

		log("account address: %s", account.getAddress());

		try {
			log("account json:"); 
			log(account.toJson().toString());

			log("account json pretty:");
			log(account.toJson(true).toString(2));
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}

		log("--- end testCreateAccount() ---");
	}
}