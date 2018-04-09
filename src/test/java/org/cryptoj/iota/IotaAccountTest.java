package org.cryptoj.iota;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.Technology;
import org.cryptoj.iota.Iota;
import org.cryptoj.iota.IotaAccount;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class IotaAccountTest extends BaseTest {

	
	public static final String MNEMONIC_WORDS_FIXED = "history suit seat regular toe valid circle public issue degree river vendor";
	public static final String SECRET_FIXED = "WZGTVNXWXTJ9SBNRYWAPZW99DPZQTDDMTRXZYOX9XRMBY9VFRGNTYGAGSJSRGDLOYCINVANLXQJGHYVUG";
	public static final String ADDRESS_FIXED = "RCJXXBJJGBXOCCRGVZYITIBTVRGYQZDTJIZELVWTYJNHFLKFQNYGCJNXLWXPJDCICQJBCGGUOVNUTQGKYFZZSOCJED";
	public static final String PASS_PHRASE = "test_pass_phrase";

	@Test
	public void verifyMatchingAddress() throws IOException, JSONException {
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(Technology.Iota, network);
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS_FIXED);
		
		Account account = protocol.createAccount(mnemonicWords, PASS_PHRASE, network);
		String secret = account.deriveSecret(mnemonicWords, PASS_PHRASE);
		String address = account.deriveAddress(secret, network);
		
		assertNotNull(account);
		assertNotNull(secret);
		assertNotNull(address);

		assertEquals(SECRET_FIXED, secret, "Secret mismatch");
		assertEquals(ADDRESS_FIXED, address, "Address mismatch");
		
		assertEquals(secret, account.getSecret());
		assertEquals(address, account.getAddress());
	}

	@Test
	public void testCreateAndRestore() throws IOException, JSONException {		
		Network network = Network.Production;
		Protocol protocol = ProtocolFactory.getInstance(Technology.Iota, network);
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

		Protocol protocol = new Iota(Network.Production);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Account account = new IotaAccount(mnemonicWords, PASS_PHRASE, protocol.getNetwork());

		log("mnemonic words: '%s'", String.join(" ", mnemonicWords));
		log("seed: '%s'", account.getSecret());

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