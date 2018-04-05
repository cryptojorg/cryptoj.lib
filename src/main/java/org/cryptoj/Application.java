package org.cryptoj;

import java.io.File;
import java.util.List;

import org.cryptoj.core.Account;
import org.cryptoj.core.Mnemonic;
import org.cryptoj.core.Network;
import org.cryptoj.core.Protocol;
import org.cryptoj.core.ProtocolFactory;
import org.cryptoj.core.Technology;
import org.cryptoj.core.Wallet;
import org.cryptoj.utility.FileUtility;
import org.cryptoj.utility.QrCodeUtility;
import org.cryptoj.utility.WalletPageUtility;

import org.json.JSONObject;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Application {

	public static final String COMMAND_NAME = "java -jar bpgw.jar";
	public static final String SWITCH_TECHNOLOGY = "-t";
	public static final String SWITCH_NETWORK = "-n";
	public static final String SWITCH_DIRECTORY = "-d";
	public static final String SWITCH_MNEMONIC = "-m";
	public static final String SWITCH_PASS_PHRASE = "-p";
	public static final String SWITCH_VERIFY = "-v";

	public static final Technology DEFAULT_TECHNOLOGY = Technology.Bitcoin;
	public static final Network DEFAULT_NETWORK = Network.Production;
	public static final String DEFAULT_DIRECTORY = System.getProperty("user.home");
	
	public static final String EXT_JSON = "json";
	public static final String EXT_HTML = "html";
	public static final String EXT_PNG = "png";

	@Parameter(names = {SWITCH_TECHNOLOGY, "--technology"}, description = "technology: (default: Bitcoin)")
	private String technology = DEFAULT_TECHNOLOGY.name();

	@Parameter(names = {SWITCH_NETWORK, "--network"}, description = "network: (default: Production)")
	private String network = DEFAULT_NETWORK.name();

	@Parameter(names = {SWITCH_DIRECTORY, "--directory"}, description = "target directory for files (default: user home )")
	private String targetDirectory = DEFAULT_DIRECTORY;

	@Parameter(names = {SWITCH_MNEMONIC, "--mnemonic"}, description = "mnemonic sentence for the wallet file (default: generate new sentence)")
	private String mnemonic;

	@Parameter(names = {SWITCH_PASS_PHRASE, "--pass-phrase"}, description = "pass phrase for the wallet file")
	private String passPhrase;

	@Parameter(names = {SWITCH_VERIFY, "--verify-wallet-file"}, description = "verify the specified wallet file")
	private String walletFile = null;

	@Parameter(names = {"-s", "--silent"}, description = "silent mode, suppress command line output")
	private boolean silent = false;

	@Parameter(names = {"-h", "--help"}, help = true)
	private boolean help;

	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.run(args);
	}

	public void run(String [] args) {
		processCommandLine(args);

		String pathToDirectory = getDirectory();
		Wallet wallet = null;

		if(isCreateMode()) {
			wallet = createWallet();
			writeFiles(wallet, pathToDirectory);
		}
		else {
			File file = getWalletFile();
			wallet = restoreWallet(file);
		}

		logWalletInfo(wallet, pathToDirectory);		
	}

	private void processCommandLine(String [] args) {
		JCommander cmd = new JCommander(this, args);
		cmd.setProgramName(COMMAND_NAME);

		if(help) {
			cmd.usage();
			System.exit(0);
		}
	}

	private Wallet createWallet() {
		Protocol protocol = getProtocol();
		List<String> mnemonicWords = getMnemonicWords(protocol);
		
		return protocol.createWallet(mnemonicWords, passPhrase);
	}
	
	private Wallet restoreWallet(File file) {
		JSONObject walletJson = FileUtility.readJsonFile(file);
		Protocol protocol = ProtocolFactory.getInstance(walletJson);
		
		return protocol.restoreWallet(walletJson, passPhrase);
	}

	private void writeFiles(Wallet wallet, String pathToDirectory) {
		log("writing wallet file ...");
		writeWalletFile(wallet, pathToDirectory);

		log("writing html and png output files ...");
		writeHtmlFile(wallet, pathToDirectory);
		writeQRCodeFile(wallet, pathToDirectory);

		log(String.format("files successfully written to directory", pathToDirectory));
	}

	private void writeWalletFile(Wallet wallet, String path) {
		String fileContent = wallet.toString();
		String fileName = getWalletFileName(wallet, path);
		FileUtility.saveToFile(fileContent, fileName);
	}

	private void writeHtmlFile(Wallet wallet, String path) {
		String fileContent = WalletPageUtility.createHtml(wallet);
		String fileName = getFileName(wallet, path, EXT_HTML);
		FileUtility.saveToFile(fileContent, fileName);
	}

	private void writeQRCodeFile(Wallet wallet, String path) {
		byte [] fileContent = QrCodeUtility.contentToPngBytes(wallet.getAccount().getAddress(), 256);
		String fileName = getFileName(wallet, path, EXT_PNG);
		FileUtility.saveToFile(fileContent, fileName);
	}

	private boolean isCreateMode() {
		return walletFile == null;
	}
	
	private File getWalletFile() {
		return new File(walletFile);
	}

	private Protocol getProtocol() {
		return ProtocolFactory.getInstance(Technology.get(technology), Network.get(network));
	}

	private String getDirectory() {
		return targetDirectory != null ? targetDirectory : DEFAULT_DIRECTORY;
	}

	private List<String> getMnemonicWords(Protocol protocol) {
		return mnemonic != null ? Mnemonic.convert(mnemonic) : protocol.generateMnemonicWords();
	}

	private String getWalletFileName(Wallet wallet, String path) {
		return getFileName(wallet, path, EXT_JSON);
	}

	private String getFileName(Wallet wallet, String path, String extension) {
		return String.format("%s%s%s", path, File.separator, wallet.getBaseName(), extension);
	}

	private void logWalletInfo(Wallet wallet, String pathToDirectory) {
		Account account = wallet.getAccount();
		String seed = account.getSecret();
		String address = wallet.getAccount().getAddress();
		String passPhrase = wallet.getPassPhrase();
		log("wallet file: " + getWalletFileName(wallet, pathToDirectory));
		log("protocol: " + wallet.getProtocol());
		log("address: " + address);
		log("encrypted: " + (passPhrase != null && passPhrase.length() > 0));
		log("pass phrase: " + passPhrase);		
		log("seed: " + seed);

		String mnemonic = Mnemonic.convert(wallet.getMnemonicWords());
		if(!mnemonic.equals(seed)) {
			log("mnemonic: " + mnemonic);
		}
	}

	private void log(String message) {
		if(!silent) {
			System.out.println(message);
		}
	}
}
