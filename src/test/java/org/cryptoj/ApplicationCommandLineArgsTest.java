package org.cryptoj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.cryptoj.common.BaseTest;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.Technology;
import org.cryptoj.core.Wallet;

import org.junit.jupiter.api.Test;

public class ApplicationCommandLineArgsTest extends BaseTest {
	
	public static final String PASS_PHRASE = "test pass phrase";
	
	/**
	 * Test default settings
	 */
	@Test
	public void testDefaultArgument() {
		Application app = new Application();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		// test create mode
		assertTrue(app.isCreateMode());
		
		// test default directory
		assertEquals(Application.DEFAULT_DIRECTORY, app.getDirectory());
		
		// create wallet wit default settings
		Wallet wallet = app.createWallet();
		Protocol protocol = wallet.getProtocol();
		
		// test pass phrase
		assertEquals(PASS_PHRASE, wallet.getPassPhrase());
		
		// test protocol
		assertEquals(Technology.Bitcoin, protocol.getTechnology());
		assertEquals(Network.Production, protocol.getNetwork());
		
		List<String> mnemonic = wallet.getMnemonicWords();
		log("some mnemonic words (using default settings): " + Mnemonic.convert(mnemonic));
		
		// test mnemonics
		assertNotNull(mnemonic);
		assertEquals(Integer.valueOf(12), Integer.valueOf(mnemonic.size()));
	}
	
	/**
	 * Test that explicit defining bitcoin test on command line works
	 */
	@Test
	public void testBitcoinTestnetProtocolArgument() {
		Application app = new Application();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_TECHNOLOGY, "bitcoin", Application.SWITCH_NETWORK, "test"};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		Wallet wallet = app.createWallet();
		Protocol protocol = wallet.getProtocol();
		
		assertEquals(Technology.Bitcoin, protocol.getTechnology());
		assertEquals(Network.Test, protocol.getNetwork());
	}
	
	/**
	 * Test that explicit ethereum local network works 
	 */
	@Test
	public void testEthereumLocalProtocolArgument() {
		Application app = new Application();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_TECHNOLOGY, "ethereum", Application.SWITCH_NETWORK, "local"};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		Wallet wallet = app.createWallet();
		Protocol protocol = wallet.getProtocol();
		
		assertEquals(Technology.Ethereum, protocol.getTechnology());
		assertEquals(Network.Local, protocol.getNetwork());
	}
	
	/**
	 * Test that explicit iota production network works 
	 */
	@Test
	public void testIotaProductionProtocolArgument() {
		Application app = new Application();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_TECHNOLOGY, "iota", Application.SWITCH_NETWORK, "production"};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		Wallet wallet = app.createWallet();
		Protocol protocol = wallet.getProtocol();
		
		assertEquals(Technology.Iota, protocol.getTechnology());
		assertEquals(Network.Production, protocol.getNetwork());
	}
	
	/**
	 * Test that providing a verify file argument leads to verify mode
	 * @throws IOException 
	 */
	@Test
	public void testCreateModeFalseArgument() throws IOException {
		Application app = new Application();
		File file = createTempFile();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_VERIFY, file.getAbsolutePath()};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		assertFalse(app.isCreateMode());
	}
	
	/**
	 * Test that providing a target directory overrides default target directory
	 * @throws IOException
	 */
	@Test
	public void testProvidedDirArgument() throws IOException {
		Application app = new Application();
		File file = createTempFile();
		String directory = file.getParent();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_DIRECTORY, directory};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		assertEquals(directory, app.getDirectory());
	}
	
	/**
	 * Test that providing the current directory overrides default target directory
	 * @throws IOException
	 */
	@Test
	public void testProvidedCurrentDirArgument() throws IOException {
		Application app = new Application();
		String directory = ".";
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_DIRECTORY, directory};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		assertEquals(directory, app.getDirectory());
	}
	
	/**
	 * Test that providing a file to verify is being used to derive the file's directory
	 * @throws IOException
	 */
	@Test
	public void testProvidedFileDirArgument() throws IOException {
		Application app = new Application();
		File file = createTempFile();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_VERIFY, file.getAbsolutePath()};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		assertEquals(file.getParent(), app.getDirectory());
	}
	
	/**
	 * Test that providing both a file and a (different) target directory has the effect that the path to the provided file has priority 
	 * @throws IOException
	 */
	@Test
	public void testProvidedDirAndWalletFileArgument() throws IOException {
		Application app = new Application();
		File file = createTempFile();
		String directory = file.getParent();
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_DIRECTORY, Application.DEFAULT_DIRECTORY, Application.SWITCH_VERIFY, file.getAbsolutePath()};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		assertEquals(directory, app.getDirectory());
	}
	
	/**
	 * Test that explicitly providing mnemonic words lead to a wallet with the exact same mnemonic words.
	 * @throws IOException
	 */
	@Test
	public void testProvidedMnemonicArgument() throws IOException {
		Application app = new Application();
		String mnemonicSentence = "explain honey inherit pause dress bus royal denial crouch fence october object";
		String [] args = {Application.SWITCH_PASS_PHRASE, PASS_PHRASE, Application.SWITCH_MNEMONIC, mnemonicSentence};
		log("testing args: " + join(args));
		app.processCommandLine(args);
		
		
		Wallet wallet = app.createWallet();
		List<String> mnemonicWords = wallet.getMnemonicWords();
		
		assertEquals(mnemonicSentence, Mnemonic.convert(mnemonicWords));
	}
	
	
}
