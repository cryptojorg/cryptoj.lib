package org.cryptoj.core;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Wallet {

	public static final String JSON_VERSION = "version";
	public static final String JSON_VERSION_VALUE = "1.0";
	public static final String JSON_ACCOUNT = "account";

	public static final String JSON_TECHNOLOGY = "technology";
	public static final String JSON_NETWORK = "network";

	public static final String JSON_FILE_EXTENSION = "json";

	private List<String> mnemonicWords = null;
	protected Account account = null;

	protected Wallet(List<String> mnemonicWords, String passPhrase, Protocol protocol) {
		processProtocol(protocol);
		processMnemonicWords(mnemonicWords, protocol);

		account = protocol.createAccount(mnemonicWords, passPhrase, protocol.getNetwork());
	}

	public Wallet(JSONObject walletJson, String passPhrase) throws JSONException {
		validateWalletJson(walletJson);

		JSONObject accountJson = walletJson.getJSONObject(JSON_ACCOUNT);
		Protocol protocol = ProtocolFactory.getInstance(walletJson);

		account = protocol.restoreAccount(accountJson, passPhrase);
	}

	protected void processProtocol(Protocol p) {
		if(p == null) {
			throw new IllegalArgumentException("Protocol must not be null");
		}
	}

	protected void processMnemonicWords(List<String> mw, Protocol protocol) {
		if(mw == null || mw.size() == 0) {
			mnemonicWords = protocol.generateMnemonicWords();
		}
		else {
			mnemonicWords = mw;
		}

		protocol.validateMnemonicWords(mnemonicWords);
	}

	public List<String> getMnemonicWords() {
		return mnemonicWords;
	}

	public abstract String getSecretLabel();

	public String getPassPhrase() {
		return account.getPassPhrase();
	}

	public Protocol getProtocol() {
		return account.getProtocol();
	}

	public Account getAccount() {
		return account;
	}

	/**
	 * Returns the wallet file name (without path information).
	 */
	public String getFileName() {
		return String.format("%s.%s", getBaseName(), getFileExtension());
	}

	/**
	 * Returns the wallet base name without path to file and without extension.
	 */
	public String getBaseName() {
		if(getAccount() == null) {
			return null;
		}

		return getAccount().getAddress();
	}

	/** 
	 * Returns the wallet file extension.
	 */
	public String getFileExtension() {
		return JSON_FILE_EXTENSION;
	}

	/**
	 * Returns wallet as JSONObject.
	 * 
	 * @throws Exception 
	 */
	public JSONObject toJson() {
		try {
			JSONObject obj = new JSONObject();

			obj.put(JSON_VERSION, JSON_VERSION_VALUE);

			Protocol p = getProtocol();
			if(p != null) {
				obj.put(JSON_TECHNOLOGY, p.getTechnology());
				obj.put(JSON_NETWORK, p.getNetwork());
			}

			boolean includeProtocolInfo = false;
			obj.put(JSON_ACCOUNT, getAccount().toJson(includeProtocolInfo));

			return obj;
		}
		catch(JSONException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected void validateWalletJson(JSONObject walletJson) throws JSONException {

		// check and extract version
		if(!walletJson.has(JSON_VERSION)) {
			throw new JSONException("Wallet has no version attribute");
		}

		if(!JSON_VERSION_VALUE.equals(walletJson.getString(JSON_VERSION))) {
			throw new JSONException("Wallet has unkonwn version. Expected value: " + JSON_VERSION_VALUE);
		}

		// check and extract protocol
		if(!walletJson.has(JSON_TECHNOLOGY)) {
			throw new JSONException("Wallet has no technology attribute");
		}

		if(!walletJson.has(JSON_NETWORK)) {
			throw new JSONException("Wallet has no network attribute");
		}

		if(!walletJson.has(JSON_ACCOUNT)) {
			throw new JSONException("Wallet has no account attribute");
		}
	}

	/**
	 * Returns content of wallet as String.
	 * May be used to write wallet to a file system.
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

		if(!(obj instanceof Wallet)) {
			return false;
		}

		Wallet other = (Wallet)obj;

		// special case mnemonic words: a restored wallet does not have mnemonic words
		// as a result this check only makes sense if both wallets have non null mnemonic words
		// TODO decide if wallet should have a flag if it has been freshly created or restored from a file
		if(mnemonicWords != null && other.mnemonicWords != null) {
			if(!mnemonicWords.equals(other.mnemonicWords)) {
				return false;
			}
		} 

		return account.equals(other.account);
	}

	@Override
	public int hashCode() {
		int mHash = mnemonicWords == null ? 0 : Mnemonic.convert(mnemonicWords).hashCode();
		int aHash = account == null ? 0 : account.hashCode();

		return mHash | aHash;
	}
}
