package org.cryptoj.ethereum;

import java.math.BigInteger;
import java.util.List;

import org.cryptoj.core.Account;
import org.cryptoj.core.Network;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule; 

public class EthereumAccount extends Account {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.registerModule(new JsonOrgModule());	
	}

	public EthereumAccount(List<String> mnemonicWords, String passPhrase, Network network) {
		super(mnemonicWords, passPhrase, new Ethereum(network));
	}
	
	public EthereumAccount(JSONObject accountJson, String passPhrase, Network network) {
		super(passPhrase, new Ethereum(network));
		
		WalletFile walletFile = objectMapper.convertValue(accountJson, WalletFile.class);
		
		try {
			Credentials credentials = Credentials.create(Wallet.decrypt(passPhrase, walletFile));
			ECKeyPair keyPair = credentials.getEcKeyPair();
			BigInteger privateKey = keyPair.getPrivateKey();
			
			// jaxx wallet does not like private key with prefix
			setSecret(Numeric.toHexStringNoPrefix(privateKey));
		    setAddress(credentials.getAddress());
		} 
		catch (CipherException e) {
			throw new RuntimeException("Failed to create credentials from provided wallet json");
		}		
	}
	
	/**
	 * Returns the private key (hex string with prefix) derived from the provided mnemonic words and pass phrase
	 * @param mnemonicWords
	 * @param passPhrase
	 */
	@Override 
	public String deriveSecret(List<String> mnemonicWords, String passPhrase) {
		String mnemonic = String.join(" ", mnemonicWords);
		byte [] seed = MnemonicUtils.generateSeed(mnemonic, passPhrase);
		byte [] privateKeyBytes = Hash.sha256(seed);
		ECKeyPair keyPair = ECKeyPair.create(privateKeyBytes);
		
		// jaxx wallet does not like private key with prefix
		return Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
	}
	
	/**
	 * Returns the address for the provided private key  
	 * @param privateKey (hex string with prefix)
	 * @param passPhrase
	 */
	@Override
	public String deriveAddress(String secret, Network network) {
		Credentials credentials = Credentials.create(secret);
		return credentials.getAddress();
	}
	
	@Override
	public JSONObject toJson(boolean includeProtocolInfo) {
		try {
			Credentials credentials = Credentials.create(getSecret());
			ECKeyPair keyPair = credentials.getEcKeyPair();
			WalletFile wallet = Wallet.createStandard(getPassPhrase(), keyPair);
			JSONObject json = objectMapper.convertValue(wallet, JSONObject.class);
			return json;
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to convert wallet to json object", e);
		}
	}
}
