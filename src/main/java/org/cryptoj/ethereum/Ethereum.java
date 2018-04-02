package org.cryptoj.ethereum;

import java.util.List;

import org.cryptoj.core.Account;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.Technology;
import org.cryptoj.core.Wallet;
import org.json.JSONObject;

public class Ethereum extends Protocol {

	public static final int MNEMONIC_LENGTH_MIN = 12;
	public static final int MNEMONIC_LENGTH_MAX = 24;

	public Ethereum(Network network) {
		super(Technology.Ethereum, network);
	}

	
	@Override
	public List<String> generateMnemonicWords() {
		// TODO re-enable once web3j is working again ...
//		byte [] entropy = Entropy.generateEntropy();
//		String mnemonicString = MnemonicUtils.generateMnemonic(entropy); 
//		return Arrays.asList(mnemonicString.split(" "));
		
		return super.generateMnemonicWords();
	}
	
	@Override
	public void validateMnemonicWords(List<String> mnemonicWords) {
		if(mnemonicWords == null) {
			throw new IllegalArgumentException("Mnemonic words must not be null");
		} 

		if(mnemonicWords.size() < MNEMONIC_LENGTH_MIN) {
			throw new IllegalArgumentException(
					String.format("Provided mnemonic word list contains less than %i words", MNEMONIC_LENGTH_MIN));
		}

		if(mnemonicWords.size() > MNEMONIC_LENGTH_MAX) {
			throw new IllegalArgumentException(
					String.format("Provided mnemonic word list contains more than %i words", MNEMONIC_LENGTH_MAX));
		}

		if(mnemonicWords.size() %3 != 0) {
			throw new IllegalArgumentException("Provided the number of words for the mnemonic word list is not a multiple of 3");
		}
	}
	
	@Override
	public Wallet createWallet(List<String> mnemonicWords, String passPhase) {
		validateMnemonicWords(mnemonicWords);
		return new EthereumWallet(mnemonicWords, passPhase, getNetwork());
	}

	@Override
	public Wallet restoreWallet(JSONObject walletJson, String passPhrase) {
		try {
			return new EthereumWallet(walletJson, passPhrase);
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to restore Ethereum wallet", e);
		} 	
	}

	@Override
	public Account createAccount(List<String> mnemonicWords, String passPhrase, Network network) {
		validateMnemonicWords(mnemonicWords);
		return new EthereumAccount(mnemonicWords, passPhrase, network);
	}

	@Override
	public Account restoreAccount(JSONObject accountJson, String passPhrase) {
		return new EthereumAccount(accountJson, passPhrase, getNetwork());
	}
}
