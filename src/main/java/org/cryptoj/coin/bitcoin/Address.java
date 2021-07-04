package org.cryptoj.coin.bitcoin;

import java.math.BigInteger;

import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Address.java : an address in a BIP44 wallet account chain
 *
 */
public class Address {
	
	public static final int TYPE_LEGACY = 0;
	public static final int TYPE_SEGWIT = 1;
	
	// controls if getAddress returns legacy or segwit address
	// with legacy the addresses produced are consistent with the electrum wallet
	// unfortunately this is not the case for segwit native
	public static final int TYPE = TYPE_LEGACY;

    private int childNum;
    private String strPath = null;
    private ECKey ecKey = null;
    private byte[] pubKey = null;
    private byte[] pubKeyHash = null;

    private NetworkParameters params = null;

    @SuppressWarnings("unused")
	private Address() { }

    /**
     * Constructor an HD address.
     *
     * @param NetworkParameters params
     * @param DeterministicKey cKey deterministic key for this address
     * @param int child index of this address in its chain
     *
     */
    public Address(NetworkParameters params, DeterministicKey cKey, int child) {

        this.params = params;
        childNum = child;

        DeterministicKey dk = HDKeyDerivation.deriveChildKey(cKey, new ChildNumber(childNum, false));
        // compressed WIF private key format
        if(dk.hasPrivKey()) {
        	byte[] getPrivKeyBytes = dk.getPrivKeyBytes();
        	byte[] prepended0Byte = new byte[1 + getPrivKeyBytes.length];
        	prepended0Byte[0] = 0;
            System.arraycopy(getPrivKeyBytes, 0, prepended0Byte, 1, getPrivKeyBytes.length);
            
            ecKey = ECKey.fromPrivate(new BigInteger(prepended0Byte), true);
        }
        else {
            ecKey = ECKey.fromPublicOnly(dk.getPubKey());
        }

        long now = Utils.now().getTime() / 1000;    // use Unix time (in seconds)
        ecKey.setCreationTimeSeconds(now);

        pubKey = ecKey.getPubKey();
        pubKeyHash = ecKey.getPubKeyHash();

        strPath = dk.getPathAsString();
    }

    /**
     * Get pubKey as byte array.
     *
     * @return byte[]
     *
     */
    public byte[] getPubKey() {
        return pubKey;
    }

    /**
     * Get pubKeyHash as byte array.
     *
     * @return byte[]
     *
     */
    public byte[] getPubKeyHash() {
        return pubKeyHash;
    }

    /**
     * Return public address for this instance.
     *
     * @return String
     *
     */
    public String getAddressString() {
		return getAddress().toString();    	
    }

    /**
     * Return private key for this address (compressed WIF format).
     *
     * @return String
     *
     */
    public String getPrivateKeyString() {

        if(ecKey.hasPrivKey()) {
            return ecKey.getPrivateKeyEncoded(params).toString();
        }
        else    {
            return null;
        }

    }

    /**
     * Return Bitcoinj address instance for this Address.
     *
     * @return org.bitcoinj.core.Address
     *
     */
    public org.bitcoinj.core.Address getAddress() {
    	if(TYPE == TYPE_LEGACY) {
        	return LegacyAddress.fromKey(params, ecKey);    		
    	}
    	
		return SegwitAddress.fromKey(params, ecKey);    	
    }

    /**
     * Return BIP44 path for this address (m / purpose' / coin_type' / account' / chain / address_index).
     *
     * @return String
     *
     */
    public String getPath() {
        return strPath;
    }

    /**
     * Write address to JSONObject.
     * For debugging only.
     *
     * @return JSONObject
     *
     */
    public JSONObject toJSON() {
    	return toJSON(false);
    }
    
    private JSONObject toJSON(boolean includeKey) {
        try {
            JSONObject obj = new JSONObject();

            obj.put("address", getAddressString());
            if(ecKey.hasPrivKey() && includeKey) {
                obj.put("key", getPrivateKeyString());
            }

            obj.put("path", getPath());

            return obj;
        }
        catch(JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}
