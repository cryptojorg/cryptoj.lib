package org.cryptoj.coin.bitcoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.cryptoj.core.Account;
import org.cryptoj.core.Network;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * BitcoinAccount.java : an address in a BIP44 wallet account chain
 *
 */
public class BitcoinAccount extends Account {

	public static final String WALLET_ELECTRUM = "Electrum";
	public static final String [] SUPPORTED_WALLETS = { WALLET_ELECTRUM };
	
	public static final String WALLET_DEFAULT = WALLET_ELECTRUM;
	
	private List<Chain> chains = null;

	/**
	 * Constructor for account.
	 *
	 * @param List<String> mnemonicWords the BIP39 seed word list for this HD account
	 * @param NetworkParameters params
	 */
	public BitcoinAccount(List<String> mnemonicWords, String passPhrase, Network network, String targetWallet) {
		super(mnemonicWords, passPhrase, new Bitcoin(network), targetWallet);

		chains = getChains(mnemonicWords, getNetwork());
	}

	public BitcoinAccount(JSONObject accountJson, String passPhrase, Network network, String targetWallet) throws JSONException {
		super(accountJson, passPhrase, new Bitcoin(network), targetWallet);
		
		List<String> mnemonicWords = new ArrayList<String>(Arrays.asList(getSecret().split(" ")));
		chains = getChains(mnemonicWords, getNetwork());
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
		
		if(WALLET_ELECTRUM.equals(getWallet())) {
			return getElectrumInfo();
		}
		
		return "";
	}
	

	@Override
	public String deriveSecret(List<String> menmonicWords) {
		return String.join(" ", menmonicWords);
	}

	
	/**
	 * Use the settings below for electrum address generation.
	 * <ul>
	 *   <li>BIP39 seed</li>
	 *   <li>p2pkh Address generation (legacy)</li>
	 *   <li>BIP44 path "m/44'/0'/0'"
	 * </ul>
	 */
	@Override
	public String deriveAddress(String secret) {
		List<String> mnemonicWords = new ArrayList<String>(Arrays.asList(secret.split(" ")));
		DeterministicKey dk = getDeterministicKey(mnemonicWords);
		Chain receiveChain = new Chain(dk, true, getNetwork());
		Address address = receiveChain.getAddressAt(0);
		
		return address.getAddressString();
	}

	@Override
	public JSONObject toJson(boolean includePrototolInfo) {
		try {
			JSONObject obj = super.toJson(includePrototolInfo);
			obj.put("chains", chainsToJson());
			return obj;
		}
		catch(JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	private List<Chain> getChains(List<String> mnemonicWords, Network network) {
		DeterministicKey dk = getDeterministicKey(mnemonicWords);
		List<Chain> chains = new ArrayList<>();
		chains.add(new Chain(dk, true, network)); // receive chain
		chains.add(new Chain(dk, false, network)); // change chain
		
		return chains;
	}

	private DeterministicKey getDeterministicKey(List<String> mnemonicWords) {
		// the passPhrase needs to be set to an empty string, otherwise the resulting addresses do not match 
		// those produced by the electrum wallet
		byte [] seed = MnemonicCode.toSeed(mnemonicWords, "");
		
		DeterministicKey rootKey = ((Bitcoin)getProtocol()).seedToRootKey(seed);
		int child = rootKey.getChildNumber().num();
		int childnum = child | ChildNumber.HARDENED_BIT;
		
		return HDKeyDerivation.deriveChildKey(rootKey, childnum);
	}

	private JSONArray chainsToJson() {
		JSONArray chainsArray = new JSONArray();
		for(Chain chain : chains)   {
			chainsArray.put(chain.toJSON());
		}
		return chainsArray;
	}

	@Override
	public String deriveAddress(String secret, int[] path) {
		return "WARNING not implemented yet";
	}
	
	private String getElectrumInfo() {
		return "1. Start with 'Create New Wallet' in Electrum.<br>"
				+ "2. Choose any new wallet name.<br>"
				+ "3. Select 'Standard Wallet'.<br>"
				+ "4. Proceed with 'I already have a seed' and check 'BIP39 seed'<br>."
				+ "5. Under Options check 'BIP39 seed'<br>."
				+ "6. Enter the mnemonics into the big field.<br>"
				+ "7. Choose 'legacy (p2pkh)' for the address type and leave the path field unchanged at m/44'/0'/0'.<br>"
				+ "8. Enter the pass phrase in the password fields.<br>"
				+ "9. Done.";
	}
	
}
