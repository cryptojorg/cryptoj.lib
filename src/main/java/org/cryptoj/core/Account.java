package org.cryptoj.core;

import java.util.List;

import org.cryptoj.utility.AesUtility;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Account {

	public static final String JSON_PROTOCOL = "protocol";
	public static final String JSON_NETWORK = "network";
	public static final String JSON_WALLET = "wallet";

	public static final String JSON_ADDRESS = "address";
	public static final String JSON_SECRET = "secret";
	public static final String JSON_ENCRYPTED = "encrypted";
	public static final String JSON_IV = "iv";

	private String address;
	private String wallet;
	private String secret;
	private String passPhrase;
	private Protocol protocol;

	public Account(String passPhrase, Protocol protocol) {
		processPassPhrase(passPhrase);
		processProtocol(protocol);
	}
	
	public Account(List<String> mnemonicWords, String passPhrase, Protocol protocol, String targetWallet) {
		this(passPhrase, protocol);
		
		processWallet(targetWallet);
				
		secret = deriveSecret(mnemonicWords);
		address = deriveAddress(secret);
	}

	public Account(JSONObject node, String passPhrase, Protocol protocol, String targetWallet) throws JSONException {
		this(passPhrase, protocol);
		
		processWallet(targetWallet);
		processAddress(node);
		processSecret(node);
	}

	/** 
	 * Checks if the provided target wallet type is supported by the current implementation
	 * @param targetWallet the desired target wallet
	 * @return true iff the provided target wallet type is supported
	 */
	public abstract boolean isSupported(String targetWallet);

	/** 
	 * Provides information regarding setup of the target wallet application.
	 * Based on this info the user is supposed to be able to create the corresponding account in the target wallet application.
	 */
	public abstract String getWalletInfo();
	
	/** 
	 * Creates the secret string using the provided mnemonic words.
	 * The resulting secret may then be used to derive the account address via @link deriveAddress.
	 * @return true iff the provided target wallet type is supported
	 */
	public abstract String deriveSecret(List<String> menmonicWords);
	
	/**
	 * Returns the account address for the specified secret
	 * @param secret used to (deterministically) derive the account address
	 * @return the first account address for this accounts target wallet
	 */
	public abstract String deriveAddress(String secret);
	
	/**
	 * Returns the account address for the specified secret and BIP44 path
	 * @param secret used to (deterministically) derive the account address
	 * @param path BIP44 path in the format of an int array
	 * @return the first account address for this accounts target wallet
	 */
	public abstract String deriveAddress(String secret, int [] path);
		
	/**
	 * Sets pass phrase member variable and converts a null value into an empty string.
	 */
	private void processPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase == null ? "" : passPhrase;
	}

	/**
	 * Extracts address member variable from provided node.
	 */
	private void processAddress(JSONObject node) throws JSONException {
		if(!node.has(JSON_ADDRESS)) {
			throw new JSONException("Account node has no address attribute");
		}

		address = node.getString(JSON_ADDRESS);
	}

	/**
	 * Extracts secret member variable from provided node and verifies against the address.
	 */
	private void processSecret(JSONObject node) throws JSONException {
		
		// check and extract seed
		if(!node.has(JSON_SECRET)) {
			throw new JSONException("Account node has no secret attribute");
		}

		if(!node.has(JSON_ENCRYPTED)) {
			throw new JSONException("Account node has no encrypted attribute");
		}

		// get secret
		if(node.getBoolean(JSON_ENCRYPTED)) {
			if(passPhrase.length() == 0) {
				throw new JSONException("No password provided for encrypted account json");
			}
			
			if(!node.has(JSON_IV)) {
				throw new JSONException("Wallet file has no IV attribute (required for encrypted seed)");
			}

			try {
				AesUtility aes = new AesUtility(passPhrase);
				String iv = node.getString(JSON_IV);
				String seedEncrypted = node.getString(JSON_SECRET);

				secret = aes.decrypt(seedEncrypted, iv);
			} 
			catch (Exception e) {
				throw new RuntimeException("Failed to decrypt account secret", e);
			}
		}
		else {
			secret = node.getString(JSON_SECRET);
		}
		
		String addressExpected = deriveAddress(secret);
		if(!address.equals(addressExpected)) {
			throw new IllegalArgumentException(
					String.format("Address verification failure. Expected '%s' but found '%s'", addressExpected, address));
		}
	}

	private void processProtocol(Protocol protocol) {
		if(protocol == null) {
			throw new IllegalArgumentException("Protocol must not be null");
		}

		this.protocol = protocol;
	}

	private void processWallet(String targetWallet) {
		if(targetWallet == null) {
			throw new IllegalArgumentException("Target wallet must not be null");
		}
		
		if(!isSupported(targetWallet)) {
			throw new IllegalArgumentException("Unsupported target wallet '" + targetWallet + "'");
		}

		this.wallet = targetWallet;
	}

	/**
	 * Convert account to JSONObject including private key/seed in plain text.
	 *
	 * @return JSONObject
	 * @throws JSONException 
	 */
	public JSONObject toJson() {
		try {
			return toJson(true);
		}
		catch(JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Convert account to JSONObject including private key/seed.
	 * @param includeProtocolInfo adds protocol attributes iff true
	 * @return JSON representation of this account
	 * @throws JSONException 
	 */
	public JSONObject toJson(boolean includeProtocolInfo) throws JSONException {
		JSONObject obj = new JSONObject();
		boolean encrypted = false;

		obj.put(JSON_WALLET, getWallet());
		obj.put(JSON_ADDRESS, getAddress());

		if(includeProtocolInfo) {
			obj.put(JSON_PROTOCOL, getProtocol());
			obj.put(JSON_NETWORK, getNetwork());
		}

		if(passPhrase == null || passPhrase.length() == 0) {
			obj.put("secret", getSecret());
		}
		else {
			try {
				AesUtility aes = new AesUtility(passPhrase);
				encrypted = true;

				obj.put(JSON_SECRET, aes.encrypt(getSecret()));
				obj.put(JSON_IV, aes.getIv());
			}
			catch (Exception e) {
				new RuntimeException(e.getMessage());
			}
		}

		obj.put(JSON_ENCRYPTED, encrypted);

		return obj;
	}
	
	public String getWallet() {
		return wallet;
	}
	
	public String getAddress() {
		return address;
	}

	public String getSecret() {
		return secret;
	}

	public String getPassPhrase() {
		return passPhrase;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public Network getNetwork() {
		return protocol == null ? null : protocol.getNetwork();
	}	

	public ProtocolEnum getProtocolEnum() {
		return protocol == null ? null : protocol.getProtocolEnum();
	}	

	/**
	 * Returns content of account as String.
	 * May be used to write account to a file system.
	 */
	@Override
	public String toString() {
		return toJson().toString().replace(",\"", ", \"");
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		if(!(obj instanceof Account)) {
			return false;
		}

		Account other = (Account)obj;

		return getSecret().equals(other.getSecret()) 
				&& getPassPhrase().equals(other.getPassPhrase()) 
				&& getAddress().equals(other.getAddress()) 
				&& getProtocol().equals(other.getProtocol());
	}

	@Override
	public int hashCode() {
		return getSecret().hashCode() | getProtocol().hashCode();
	}
}
