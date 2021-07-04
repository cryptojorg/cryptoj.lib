package org.cryptoj.ethereum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.cryptoj.coin.ethereum.EthereumAccount;
import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Network;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;

public class AccountBip44PathsTest extends BaseTest {

	public static final String MNEMONIC_WORDS_FIXED = "expose dwarf coyote broken alert rifle fade novel estate output about repair";
	public static final String PASS_PHRASE = "test pass phrase";

	public static final int H = EthereumAccount.HARDENED;
	public static final int [] PATH_44H_60H_0H = {44|H, 60|H, 0|H};
	public static final int [] PATH_44H_60H_0H_0 = {44|H, 60|H, 0|H, 0};
	public static final int [] PATH_44H_60H_0H_0_0 = {44|H, 60|H, 0|H, 0, 0};
	
	public static String ADDR_PP_44H_60H_0H = "0x6C9BeF11b00764C0de04317C69c6BeB2E1b1f84F";
	public static String ADDR_PP_44H_60H_0H_0 = "0xC220E759A0083E5678b42D7CDA5bDb07259F0C31";
	public static String ADDR_PP_44H_60H_0H_0_0 = "0xCC78cf4F160dBa3De11Bb5401Cb19d64e685c2Ac";
	
	public static String ADDR_NPP_44H_60H_0H = "0x6f441b62c491FE767968B0FA09Da9306BD866f08";
	public static String ADDR_NPP_44H_60H_0H_0 = "0x338c8bc615365E550d22909603eb817606775F45";
	public static String ADDR_NPP_44H_60H_0H_0_0 = "0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f";
	
	@Test
	public void testAddressesDerivedFromBip44Paths() {
		
		// web3j approach
		String a_w3j_pp_44H_60H_0H = s2a_w3j(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H);
		String a_w3j_pp_44H_60H_0H_0 = s2a_w3j(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H_0);
		String a_w3j_pp_44H_60H_0H_0_0 = s2a_w3j(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H_0_0);
		
		String a_w3j_npp_44H_60H_0H = s2a_w3j(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H);
		String a_w3j_npp_44H_60H_0H_0 = s2a_w3j(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H_0);
		String a_w3j_npp_44H_60H_0H_0_0 = s2a_w3j(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H_0_0);
		
		System.out.println("address passphrase (web3j, issue 919: m/44'/60'/0'): " + a_w3j_pp_44H_60H_0H);
		System.out.println("address passphrase (web3j, issue 919: m/44'/60'/0'/0): " + a_w3j_pp_44H_60H_0H_0);
		System.out.println("address passphrase (web3j, issue 919: m/44'/60'/0'/0/0 (MyCrypto)): " + a_w3j_pp_44H_60H_0H_0_0);
		
		assertEquals(ADDR_PP_44H_60H_0H, a_w3j_pp_44H_60H_0H);
		assertEquals(ADDR_PP_44H_60H_0H_0, a_w3j_pp_44H_60H_0H_0);
		assertEquals(ADDR_PP_44H_60H_0H_0_0, a_w3j_pp_44H_60H_0H_0_0);
		
		System.out.println("address nopassphrase (web3j, issue 919: m/44'/60'/0'): " + a_w3j_npp_44H_60H_0H);
		System.out.println("address nopassphrase (web3j, issue 919: m/44'/60'/0'/0): " + a_w3j_npp_44H_60H_0H_0);
		System.out.println("see https://github.com/web3j/web3j/issues/919");
		System.out.println("see https://github.com/MetaMask/metamask-extension/search?q=bip44");	
		System.out.println("address nopassphrase (web3j, issue 919: m/44'/60'/0'/0/0 (MyCrypto, MetaMask)): " + a_w3j_npp_44H_60H_0H_0_0);
		
		assertEquals(ADDR_NPP_44H_60H_0H, a_w3j_npp_44H_60H_0H);
		assertEquals(ADDR_NPP_44H_60H_0H_0, a_w3j_npp_44H_60H_0H_0);
		assertEquals(ADDR_NPP_44H_60H_0H_0_0, a_w3j_npp_44H_60H_0H_0_0);
		
		// cryptoj approach
		String a_cj_pp_44H_60H_0H = s2a_cj(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H);
		String a_cj_pp_44H_60H_0H_0 = s2a_cj(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H_0);
		String a_cj_pp_44H_60H_0H_0_0 = s2a_cj(MNEMONIC_WORDS_FIXED, PASS_PHRASE, PATH_44H_60H_0H_0_0);
		
		String a_cj_npp_44H_60H_0H = s2a_cj(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H);
		String a_cj_npp_44H_60H_0H_0 = s2a_cj(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H_0);
		String a_cj_npp_44H_60H_0H_0_0 = s2a_cj(MNEMONIC_WORDS_FIXED, null, PATH_44H_60H_0H_0_0);
		
		// ensure that cryptoj approach is compliant with web3j approach
		assertEquals(a_w3j_pp_44H_60H_0H, a_cj_pp_44H_60H_0H);
		assertEquals(a_w3j_pp_44H_60H_0H_0, a_cj_pp_44H_60H_0H_0);
		assertEquals(a_w3j_pp_44H_60H_0H_0_0, a_cj_pp_44H_60H_0H_0_0);
		
		assertEquals(a_w3j_npp_44H_60H_0H, a_cj_npp_44H_60H_0H);
		assertEquals(a_w3j_npp_44H_60H_0H_0, a_cj_npp_44H_60H_0H_0);
		assertEquals(a_w3j_npp_44H_60H_0H_0_0, a_cj_npp_44H_60H_0H_0_0);
	}
	
	private String s2a_w3j(String mnemonic, String pass_phrase, int [] path) {
		byte [] seed = MnemonicUtils.generateSeed(mnemonic, pass_phrase);
	    Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
	    Bip32ECKeyPair bip44Keypair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path);
	    Credentials credentials = Credentials.create(bip44Keypair);
		String address = credentials.getAddress();
		String addressCheckSummed = Keys.toChecksumAddress(address);
		
		return addressCheckSummed;
	}

	private String s2a_cj(String mnemonic, String pass_phrase, int [] path) {
		List<String> words = Arrays.asList(mnemonic.split(" "));
		EthereumAccount a = new EthereumAccount(words, pass_phrase, Network.Production, EthereumAccount.WALLET_DEFAULT);
		String secret = a.deriveSecret(words);
		String address = a.deriveAddress(secret, path);
		
		return address;
	}
}
