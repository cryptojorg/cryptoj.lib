package org.cryptoj.core;

import java.util.List;

import org.cryptoj.utility.EntropyUtility;
import org.json.JSONObject;

public abstract class Protocol {

	private ProtocolEnum protocol;
	private Network network;

	public Protocol(ProtocolEnum protocol, Network network) {
		processProtocol(protocol);
		processNetwork(network);
	}

	/**
	 * Provides the default target wallet type for this account.
	 * Concrete classes need to implement this method as a static method.
	 * @return
	 */
	abstract public String defaultTargetWallet();
	
	abstract public Wallet createWallet(List<String> mnemonicWords, String passPhase, String targetWallet);
	abstract public Wallet restoreWallet(JSONObject walletJson, String passPhrase);

	abstract public Account createAccount(List<String> mnemonicWords, String passPhrase, Network network, String targetWallet);
	abstract public Account restoreAccount(JSONObject accountJson, String passPhrase, String targetWallet);

	abstract public void validateMnemonicWords(List<String> mnemonicWords);
	
	public List<String> generateMnemonicWords() {
		byte [] entropy = EntropyUtility.generateEntropy();
		return Mnemonic.deriveWords(entropy);
	}

	private void processNetwork(Network n) {
		if(n == null) {
			throw new IllegalArgumentException("Network must not be null");
		}

		network = n;
	}

	private void processProtocol(ProtocolEnum p) {
		if(p == null) {
			throw new IllegalArgumentException("Technology must not be null");
		}

		protocol = p;
	}

	public ProtocolEnum getProtocolEnum() {
		return protocol;
	}

	public Network getNetwork() {
		return network;
	}

	@Override
	public String toString() {
		return String.format("%s (%s)", getProtocolEnum(), getNetwork());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}

		if(!(obj instanceof Protocol)) {
			return false;
		}

		Protocol other = (Protocol)obj;

		return protocol == other.protocol && network == other.network;
	}

	@Override
	public int hashCode() {
		return 1000 * protocol.hashCode() + network.hashCode();
	}
}
