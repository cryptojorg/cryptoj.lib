package org.cryptoj.core;

import org.cryptoj.coin.bitcoin.Bitcoin;
import org.cryptoj.coin.ethereum.Ethereum;
import org.json.JSONException;
import org.json.JSONObject;

public class ProtocolFactory {

	public static Protocol getInstance(ProtocolEnum protocol, Network network) {
		switch(protocol) {
		case Bitcoin: 
			return new Bitcoin(network);
		case Ethereum: 
			return new Ethereum(network);
		default:
			throw new IllegalArgumentException(String.format("Protocol %s is currently not supported", protocol));
		}
	}

	public static Protocol getInstance(JSONObject walletJson) {
		try {
			ProtocolEnum protocol = ProtocolEnum.get(walletJson.getString(Wallet.JSON_PROTOCOL));
			Network network= Network.get(walletJson.getString(Wallet.JSON_NETWORK));

			return getInstance(protocol, network);
		}
		catch(JSONException e) {
			throw new RuntimeException("Failed to get protocol instance", e);
		}
	}
}
