package org.cryptoj.bitcoin;

import java.util.List;

import org.cryptoj.core.Network;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.Technology;
import org.cryptoj.core.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

public class BitcoinWallet extends Wallet {
	
	public static final String SECRET_LABEL = "Mnemonic Seed";

	public BitcoinWallet(JSONObject walletJson, String passPhrase) throws JSONException {
		super(walletJson, passPhrase);
	}

	public BitcoinWallet(List<String> mnemonicWords, String passPhrase, Network network) {
		super(mnemonicWords, passPhrase, ProtocolFactory.getInstance(Technology.Bitcoin, network));
	}

	@Override
	public String getSecretLabel() {
		return SECRET_LABEL;
	}
}
