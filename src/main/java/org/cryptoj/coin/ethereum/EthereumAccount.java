package org.cryptoj.coin.ethereum;

import java.util.List;

import org.cryptoj.core.Account;
import org.cryptoj.core.Network;

import org.json.JSONObject;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule; 

public class EthereumAccount extends Account {
	
	public static final String WALLET_METAMASK = "MetaMask";
	public static final String [] SUPPORTED_WALLETS = { WALLET_METAMASK };

	public static final String WALLET_DEFAULT = WALLET_METAMASK;

	public static final int HARDENED = Bip32ECKeyPair.HARDENED_BIT;
	
	/**
	 * corresponds to BIP44 path m/44'/60'/0'/0/0
	 */
	public static int [] BIP44_PATH_DEFAULT = {44|HARDENED, 60|HARDENED, 0|HARDENED, 0, 0};
	
	// see https://github.com/web3j/web3j/issues/919
	// web3j & bitcoinj -> // m/44'/60'/0'/0 (as commented in org/web3j/crypto/Bip44WalletUtils.java)
	// metamask, blockchain.com & other javascript based libs -> "m/44'/60'/0'/0/0"
	public static int [] BIP44_PATH_METAMASK = BIP44_PATH_DEFAULT;
	
	// the mycrypto ui suggests that the bip path can be explicitly defined.
	// however, the shown path in the ui "m/44'/60'/0'/0" corresponds to the path "m/44'/60'/0'/0/0" for web3j
	public static int [] BIP44_PATH_MYCRYPTO = BIP44_PATH_DEFAULT;

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new JsonOrgModule());	
	}
	

	public EthereumAccount(List<String> mnemonicWords, String passPhrase, Network network, String targetWallet) {
		super(mnemonicWords, passPhrase, new Ethereum(network), targetWallet);
	}
	
	public EthereumAccount(JSONObject accountJson, String passPhrase, Network network, String targetWallet) {
		super(accountJson, passPhrase, new Ethereum(network), targetWallet);
	}

	@Override
	public boolean isSupported(String targetWallet) {
		if(targetWallet == null) {
			return false;
		}
		
		for(String supportedWallet: SUPPORTED_WALLETS) {
			if(targetWallet.equals(supportedWallet)) {
				return true;
			}
		}
		
		return false;
	}
	

	@Override
	public String getWalletInfo() {
		
		if(WALLET_METAMASK.equals(getWallet())) {
			return getMetamaskInfo();
		}
		
		return "";
	}
	
	/**
	 * Current implementation: Returns "(mnemonic)#(pass phrase)" or "(mnemonic)#" if the pass phrase is null or empty.
	 * 
	 * Goal: Returns the private key (hex string with prefix) derived from the provided mnemonic words and pass phrase
	 * @param mnemonicWords
	 * @param passPhrase
	 */
	@Override 
	public String deriveSecret(List<String> mnemonicWords) {
		String mnemonic = String.join(" ", mnemonicWords);
		String passPhrase = getPassPhrase();
		
		if(passPhrase == null || passPhrase.length() == 0) {
			return mnemonic + "#";
		}
		
		return mnemonic + "#" + passPhrase;
	}
	
	
	/**
	 * Returns the address for the provided secret.
	 * @param secret as produced by method {@link deriveSecret}.
	 * @return ethereum address
	 */
	@Override
	public String deriveAddress(String secret) {
		
		if(WALLET_METAMASK.equals(getWallet())) {
			return getMetamaskAddress(secret, getNetwork());
		}
		
		return deriveAddress(secret, BIP44_PATH_DEFAULT);
	}

	
	/**
	 * Returns the address for the provided secret and path.
	 * @param secret as produced by method {@link deriveSecret}.
	 * @param path numbers as https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki, https://github.com/satoshilabs/slips/blob/master/slip-0044.md
	 * @return ethereum address
	 */
	@Override
	public String deriveAddress(String secret, int[] path) {
		int split_idx = secret.indexOf("#");
		String mnemonic = secret.substring(0, split_idx);
		String pass_phrase = secret.substring(split_idx + 1);
		byte [] seed = null;
		
		if(pass_phrase != null && pass_phrase.length() > 0) {
			seed = MnemonicUtils.generateSeed(mnemonic, pass_phrase);
		}
		else {
			seed = MnemonicUtils.generateSeed(mnemonic, null);			
		}
		
		Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed);
		Bip32ECKeyPair derivedKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path);
		Credentials credentials = Credentials.create(derivedKeyPair);
		String address = credentials.getAddress();
		String checksumAddress = Keys.toChecksumAddress(address);
		
		return checksumAddress;
	}
	
	
	/**
	 * Metamask specific address generation.
	 * Metamask specifics: 
	 * <ol> 
	 *   <li>generated address does not depend on pass phrase.</li>
	 *   <li>bip44 path used: m/44'/60'/0'/0/0.</li>
	 * </ol>
	 * See {@linkplain https://github.com/web3j/web3j/issues/919} for details:
	 * web3j and bitcoinj use path m/44'/60'/0'/0 (as commented in org/web3j/crypto/Bip44WalletUtils.java)
	 * metamask, blockchain.com and other javascript based libs use path "m/44'/60'/0'/0/0".
	 * @param secret
	 * @param network
	 * @return
	 */
	private String getMetamaskAddress(String secret, Network network) {
		
		int split_idx = secret.indexOf("#");
		String mnemonic = secret.substring(0, split_idx);
		String secretMetamask = mnemonic + "#";
		
		return deriveAddress(secretMetamask, BIP44_PATH_METAMASK);
	}
	
	private String getMetamaskInfo() {
		return "1. Start with 'import using Secret Recovery Phrase' in MetaMask.<br>"
				+ "2. Use the mnemonic from this paper wallet for this.<br>"
				+ "3. Enter any password for MetaMask.<br>"
				+ "4. Done.<br>"
				+ "<br>"
				+ "Additional info: MetaMask address generation internally works with BIP44 path 'm/44'/60'/0'/0/0' and an empty pass phrase.";
	}
}
