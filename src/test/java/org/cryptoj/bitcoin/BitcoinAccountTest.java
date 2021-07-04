package org.cryptoj.bitcoin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.cryptoj.coin.bitcoin.Bitcoin;
import org.cryptoj.coin.bitcoin.BitcoinAccount;
import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.ProtocolEnum;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class BitcoinAccountTest extends BaseTest {
	
	public static final String MNEMONIC_WORDS = "expose dwarf coyote broken alert rifle fade novel estate output about repair";
	public static final String SECRET_FIXED = MNEMONIC_WORDS;
	public static final String ADDRESS = "1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1";
	public static final String PASS_PHRASE = "test pass phrase";
	
	public static final ProtocolEnum BITCOIN = ProtocolEnum.Bitcoin;
	public static final Network PRODUCTION = Network.Production;
	public static final String ELECTRUM = BitcoinAccount.WALLET_ELECTRUM;

	// https://medium.com/@buddhasource/bitcoin-legacy-vs-segwit-wallet-address-what-is-the-difference-cb2e71ab8381
	// segwit native address (using electrum) -> bc1qzktclkj5l7nwlypkqlperna8pvjr8y6k0z4ggt
	// bitcoinj provides legacy addresses that are consistent with electrum (-> for now, stick to legacy addresses)
	@Test
	public void verifyMatchingAddress() throws IOException, JSONException {
		Protocol protocol = ProtocolFactory.getInstance(BITCOIN, PRODUCTION);
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS);
		
		Account account = protocol.createAccount(mnemonicWords, PASS_PHRASE, PRODUCTION, ELECTRUM);
		String secret = account.deriveSecret(mnemonicWords);
		String address = account.deriveAddress(secret);
		
		assertNotNull(account);
		assertNotNull(secret);
		assertNotNull(address);

		assertEquals(SECRET_FIXED, secret, "Secret mismatch");
		assertEquals(ADDRESS, address, "Address mismatch");
		
		assertEquals(secret, account.getSecret());
		assertEquals(address, account.getAddress());
	}
	
	@Test
	public void testCreateAndRestore() throws IOException, JSONException {
		Protocol protocol = ProtocolFactory.getInstance(ProtocolEnum.Bitcoin, PRODUCTION);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Account accountNew = protocol.createAccount(mnemonicWords, PASS_PHRASE, PRODUCTION, ELECTRUM);

		assertNotNull(accountNew);
		
		JSONObject json = accountNew.toJson(false);
		Account accountRestored = protocol.restoreAccount(json, PASS_PHRASE, ELECTRUM);
		
		assertEquals(accountNew, accountRestored);
	}

	@Test
	public void testCreateAccount() throws IOException {
		log("--- start testCreateAccount() ---");
		
		Protocol protocol = new Bitcoin(Network.Production);
		List<String> mnemonicWords = protocol.generateMnemonicWords();
		Account account = new BitcoinAccount(mnemonicWords, PASS_PHRASE, protocol.getNetwork(), BitcoinAccount.WALLET_ELECTRUM);
		
		log("mnemonic words (bip39 seed): '%s'", String.join(" ", mnemonicWords));
		
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