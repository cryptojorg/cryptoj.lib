package org.cryptoj.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.cryptoj.common.BaseTest;
import org.junit.jupiter.api.Test;

public class EntropyUtilityTest extends BaseTest {


	/**
	 * Test expectation that for default number of entropy bits we will find not find collisions
	 * @throws Exception
	 */
	@Test
	public void testRandomness() throws Exception {
		Set<String> samples = new HashSet<>();
		int steps = 100000;

		String osName = System.getProperty("os.name");
		
		if(!osName.toLowerCase().contains("mac")) {
			log("running on " + osName + ": skipping test as this would take too long");
			steps = 0;
	    }
		
		for(int i = 0; i < steps; i++) {
			byte [] entropy = EntropyUtility.generateEntropy();
			String entropyEncoded = AesUtility.bytesToBase64(entropy);

			assertEquals(EntropyUtility.ENTROPY_BITS_DEFAULT/8, entropy.length, "Unexpected entropy size");
			assertTrue(!samples.contains(entropyEncoded), String.format("Identical entropy '%s' produced (step %d/%s)", entropyEncoded, i, steps));

			samples.add(entropyEncoded);
		}
	}

	/**
	 * Test expectation that for very few (16) entropy bits we will find collisions
	 * @throws Exception
	 */
	@Test
	public void testRandomness16() throws Exception {
		log("--- start testRandomness16() ---");
		
		Set<String> samples = new HashSet<>();
		int entropyBits = 16;
		int steps = 66000;
		boolean found = false;
		String osName = System.getProperty("os.name");
		
		if(!osName.toLowerCase().contains("mac")) {
			log("running on " + osName + ": skipping test as this would take too long");
			steps = 0;
	    }

		for(int i = 0; i < steps && !found; i++) {
			byte [] entropy = EntropyUtility.generateEntropy(entropyBits);
			String entropyEncoded = AesUtility.bytesToBase64(entropy);

			assertEquals(entropyBits/8, entropy.length, "Unexpected entropy size");

			if(samples.contains(entropyEncoded)) {
				log(String.format("Identical entropy '%s' produced (step %d/%s)", entropyEncoded, i, steps));
				found = true;
			}
			else {
				samples.add(entropyEncoded);
			}
		}
		
		if(steps > 0) {
			assertTrue(found, "Failed to create twice same entropy");
		}
		
		log("--- end testRandomness16() ---");
	}


	/**
	 * Test expectation that for many entropy bits (256) we will find not find collisions
	 * @throws Exception
	 */
	@Test
	public void testRandomness256() throws Exception {
		Set<String> samples = new HashSet<>();
		int entropyBits = 256;
		int steps = 100000;
		String osName = System.getProperty("os.name");
		
		if(!osName.toLowerCase().contains("mac")) {
			log("running on " + osName + ": skipping test as this would take too long");
			steps = 0;
	    }

		for(int i = 0; i < steps; i++) {
			byte [] entropy = EntropyUtility.generateEntropy(entropyBits);
			String entropyEncoded = AesUtility.bytesToBase64(entropy);

			assertEquals(entropyBits/8, entropy.length, "Unexpected entropy size");
			assertTrue(!samples.contains(entropyEncoded), String.format("Identical entropy '%s' produced (step %d/%s)", entropyEncoded, i, steps));

			samples.add(entropyEncoded);
		}
	}

	@Test
	public void testParameterNegative() throws Exception {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { EntropyUtility.generateEntropy(-1); } );
		log("testParameterNegative(): " + e.getMessage());
	}

	@Test
	public void testParameterZero() throws Exception {		
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { EntropyUtility.generateEntropy(0); } );
		log("testParameterZero(): " + e.getMessage());
	}

	@Test
	public void testParameterNotMultipleOf8() throws Exception {		
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> { EntropyUtility.generateEntropy(7); } );
		log("testParameterNotMultipleOf8(): " + e.getMessage());
	}
}
