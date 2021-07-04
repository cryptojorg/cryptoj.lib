package org.cryptoj.coin.ethereum;

import java.util.List;

import org.cryptoj.core.Network;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.ProtocolEnum;
import org.cryptoj.core.Wallet;
import org.json.JSONObject;

public class EthereumWallet extends Wallet {
	
	public static final String SECRET_LABEL = "Private Key";

	public EthereumWallet(JSONObject walletJson, String passPhrase) throws Exception {
		super(walletJson, passPhrase);
	}

	public EthereumWallet(List<String> mnemonicWords, String passPhrase, Network network, String targetWallet) {
		super(mnemonicWords, passPhrase, ProtocolFactory.getInstance(ProtocolEnum.Ethereum, network), targetWallet);
	}

	@Override
	public String getSecretLabel() {
		return SECRET_LABEL;
	}
}
