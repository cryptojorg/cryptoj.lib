package org.cryptoj.core;

import org.cryptoj.bitcoin.Bitcoin;
import org.cryptoj.ethereum.Ethereum;
import org.cryptoj.iota.Iota;
import org.json.JSONException;
import org.json.JSONObject;

public class ProtocolFactory {

	public static Protocol getInstance(Technology technology, Network network) {
		switch(technology) {
		case Bitcoin: 
			return new Bitcoin(network);
		case Ethereum: 
			return new Ethereum(network);
		case Iota: 
			return new Iota(network);
		default:
			throw new IllegalArgumentException(String.format("Technology %s is currently not supported", technology));
		}
	}

	public static Protocol getInstance(JSONObject walletJson) {
		try {
			Technology technology = Technology.get(walletJson.getString(Wallet.JSON_TECHNOLOGY));
			Network network= Network.get(walletJson.getString(Wallet.JSON_NETWORK));

			return getInstance(technology, network);
		}
		catch(JSONException e) {
			throw new RuntimeException("Failed to get protocol instance", e);
		}
	}
}
