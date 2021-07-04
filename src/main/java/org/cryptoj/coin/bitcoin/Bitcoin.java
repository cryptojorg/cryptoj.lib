package org.cryptoj.coin.bitcoin;

import java.util.List;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.params.UnitTestParams;

import org.cryptoj.core.Account;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolEnum;
import org.cryptoj.core.Wallet;

import org.json.JSONException;
import org.json.JSONObject;

public class Bitcoin extends Protocol {

	public static final int MNEMONIC_LENGTH_MIN = 12;
	public static final int MNEMONIC_LENGTH_MAX = 24;

	public Bitcoin(Network network) {
		super(ProtocolEnum.Bitcoin, network);
	}

	@Override
	public void validateMnemonicWords(List<String> mnemonicWords) {
		if(mnemonicWords == null) {
			throw new IllegalArgumentException("Provided mnemonic word list is null");
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

		try { 
			MnemonicCode.toSeed(mnemonicWords, "dummy");
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Provided mnemonic word list fails to verify: ", e);
		}
	}

	@Override
	public Wallet createWallet(List<String> mnemonicWords, String passPhase, String targetWallet) {
		validateMnemonicWords(mnemonicWords);
		return new BitcoinWallet(mnemonicWords, passPhase, getNetwork(), targetWallet);
	}

	@Override
	public Wallet restoreWallet(JSONObject walletJson, String passPhrase) {
        // TODO verifier.checkPreconditions(json) // and write tests for it

		try {
			return new BitcoinWallet(walletJson, passPhrase);
		}
		catch (JSONException e) {
			throw new IllegalArgumentException("Failed to restore Bitcoin wallet", e);
		} 	
	}

	@Override
	public BitcoinAccount createAccount(List<String> mnemonicWords, String passPhrase, Network network, String targetWallet) {		
		validateMnemonicWords(mnemonicWords);
		return new BitcoinAccount(mnemonicWords, passPhrase, network, targetWallet);
	}

	@Override
	public Account restoreAccount(JSONObject accountJson, String passPhrase, String targetWallet) {
		try {
			return new BitcoinAccount(accountJson, passPhrase, getNetwork(), targetWallet);
		} 
		catch (JSONException e) {
			throw new IllegalArgumentException("Failed to create Bitcoin account from json object", e);
		}
	}


	@Override
	public String defaultTargetWallet() {
		return BitcoinAccount.WALLET_DEFAULT;
	}
	
	
	public static NetworkParameters getNetworkParameters(Network network) {
		if(Network.Production.equals(network)) {
			return MainNetParams.get();
		}
		else if(Network.Test.equals(network)) {
			return TestNet3Params.get();
		}
		else {
			return UnitTestParams.get();
		}
	}

	// TODO check/verify to create segwit keys, see
	// https://www.reddit.com/r/Electrum/comments/7dku5r/segwit_wallets_and_electrum/
	// hypothesis only need to change constant 44 to 49
	public DeterministicKey seedToRootKey(byte [] seed) {
		DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
		DeterministicKey childKey = HDKeyDerivation.deriveChildKey(masterPrivateKey, 44 | ChildNumber.HARDENED_BIT);

		return  HDKeyDerivation.deriveChildKey(childKey, ChildNumber.HARDENED_BIT);
	}
}
