package org.cryptoj.coin.bitcoin;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.cryptoj.core.Network;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Chain.java : a chain in a BIP44 wallet account
 *
 */
public class Chain {

    private DeterministicKey cKey = null;
    private boolean isReceive;
    private Network network = null;

    @SuppressWarnings("unused")
	private Chain() { }

    /**
     * Constructor for a chain.
     *
     * @param NetworkParameters params
     * @param DeterministicKey aKey deterministic key for this chain
     * @param boolean isReceive this is the receive chain
     *
     */
    public Chain(DeterministicKey aKey, boolean isReceive, Network network) {

        this.network = network;
        this.isReceive = isReceive;
        int chain = isReceive ? 0 : 1;
        cKey = HDKeyDerivation.deriveChildKey(aKey, chain);
    }

    /**
     * Test if this is the receive chain.
     *
     * @return boolean
     */
    public boolean isReceive() {
        return isReceive;
    }

    /**
     * Return BitcoinAccount at provided index into chain.
     *
     * @return BitcoinAccount
	 *
     */
    public Address getAddressAt(int addrIdx) {
        NetworkParameters params = Bitcoin.getNetworkParameters(network);    	
        return new Address(params, cKey, addrIdx);
    }

    /**
     * Return BIP44 path for this chain (m / purpose' / coin_type' / account' / chain).
     *
     * @return String
     *
     */
    public String getPath() {
        return cKey.getPathAsString();
    }

    /**
     * Write chain to JSONObject.
     * For debugging only.
     *
     * @return JSONObject
     *
     */
    public JSONObject toJSON() {
        try {
            JSONObject obj = new JSONObject();
            NetworkParameters params = Bitcoin.getNetworkParameters(network);

            obj.put("path", getPath());

            JSONArray addresses = new JSONArray();
            for(int i = 0; i < 2; i++) {
                Address addr = new Address(params, cKey, i);
                addresses.put(addr.toJSON());
            }
            obj.put("addresses", addresses);

            return obj;
        }
        catch(JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

}
