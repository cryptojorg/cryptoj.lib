package org.cryptoj.iota;

import java.util.List;

import org.cryptoj.core.Network;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.Technology;
import org.cryptoj.core.Wallet;
import org.json.JSONObject;

public class IotaWallet extends Wallet {
	
	public static final String SECRET_LABEL = "Seed";

	public IotaWallet(JSONObject walletJson, String passPhrase) throws Exception {
		super(walletJson, passPhrase);
	}

	public IotaWallet(List<String> mnemonicWords, String passPhrase, Network network) {
		super(mnemonicWords, passPhrase, ProtocolFactory.getInstance(Technology.Iota, network));
	}

	@Override
	public String getSecretLabel() {
		return SECRET_LABEL;
	}
}
