package org.cryptoj.ethereum;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.Technology;
import org.cryptoj.ethereum.Ethereum;
import org.cryptoj.ethereum.EthereumAccount;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class EthereumAccountTest extends BaseTest {
	
	public static final String MNEMONIC_WORDS_FIXED = "history suit seat regular toe valid circle public issue degree river vendor";
	public static final String SECRET_FIXED = "b0507b391821757ffc8b3cd7ee8dd1b96f7cb05f7fcce2fb650ce1804159f7b0";
	public static final String ADDRESS_FIXED = "0x7612ba8356851158ca0d21b23a3322dbf81c2938";
	public static final String PASS_PHRASE = "test_pass_phrase";


	@Test
	public void testCreate() throws Exception {
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(Technology.Ethereum, network);
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS_FIXED);
		
		Account account = protocol.createAccount(mnemonicWords, PASS_PHRASE, network);
		log("" + account);
	}
	
	@Test
	public void verifyMatchingAddress() throws IOException, JSONException {
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(Technology.Ethereum, network);
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS_FIXED);
		
		Account account = protocol.createAccount(mnemonicWords, PASS_PHRASE, network);
		String secret = account.deriveSecret(mnemonicWords, PASS_PHRASE);
		String address = account.deriveAddress(secret, network);
		
		assertNotNull(account);
		assertNotNull(secret);
		assertNotNull(address);

		assertEquals("Secret mismatch", SECRET_FIXED, secret);
		assertEquals("Address mismatch", ADDRESS_FIXED, address);
		
		assertEquals(secret, account.getSecret());
		assertEquals(address, account.getAddress());
	}

	@Test
	public void testCreateAndRestore() throws IOException, JSONException {
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(Technology.Ethereum, network);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Account accountNew = protocol.createAccount(mnemonicWords, PASS_PHRASE, network);

		assertNotNull(accountNew);
		
		JSONObject json = accountNew.toJson(false);
		Account accountRestored = protocol.restoreAccount(json, PASS_PHRASE);
		
		assertEquals(accountNew, accountRestored);
	}

	@Test
	public void testCreateAccount() throws IOException {
		log("--- start testCreateAccount() ---");

		Protocol protocol = new Ethereum(Network.Production);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Account account = new EthereumAccount(mnemonicWords, PASS_PHRASE, protocol.getNetwork());

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