package org.cryptoj.ethereum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.cryptoj.coin.ethereum.EthereumAccount;
import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolEnum;
import org.cryptoj.core.ProtocolFactory;
import org.junit.jupiter.api.Test;

public class AccountMetaMaskTest extends BaseTest {

	public static final String MNEMONIC_WORDS = "expose dwarf coyote broken alert rifle fade novel estate output about repair";
	public static final String ADDRESS = "0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f";
	public static final String PASS_PHRASE = "test pass phrase";
	public static final String SECRET = MNEMONIC_WORDS + "#" + PASS_PHRASE;	
	
	
	public static final ProtocolEnum ETHEREUM = ProtocolEnum.Ethereum;
	public static final Network PRODUCTION = Network.Production;
	public static final String METAMASK = EthereumAccount.WALLET_METAMASK;	

	/**
	 * for the metamask address generation metamask not use the entered pass phrase.
	 * the address always looks as if no pass phrase has been entered.
	 */
	@Test
	public void testMetamaskAccount()  {
		Protocol ethereum = ProtocolFactory.getInstance(ETHEREUM, PRODUCTION);
		List<String> mnemonicWords = Mnemonic.convert(MNEMONIC_WORDS);
		
		Account account = ethereum.createAccount(mnemonicWords, PASS_PHRASE, PRODUCTION, METAMASK);
		String secret = account.deriveSecret(mnemonicWords);
		String address = account.deriveAddress(secret);
		
		assertEquals(ADDRESS, address, "Address mismatch");
		assertEquals(SECRET, secret, "Secret mismatch");
		
		assertEquals(secret, account.getSecret());
		assertEquals(address, account.getAddress());
	}	

}
