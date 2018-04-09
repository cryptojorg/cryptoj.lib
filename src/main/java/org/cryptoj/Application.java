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

	@Parameter(names = {SWITCH_TECHNOLOGY, "--technology"}, description = "technology")
	private String technology = DEFAULT_TECHNOLOGY.name();

	@Parameter(names = {SWITCH_NETWORK, "--network"}, description = "network")
	private String network = DEFAULT_NETWORK.name();

	@Parameter(names = {SWITCH_DIRECTORY, "--directory"}, description = "target directory for files")
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

	private Wallet wallet = null;

	public static void main(String[] args) throws Exception {
		Application app = new Application();
		app.run(args);
	}

	public void run(String [] args) {
		processCommandLine(args);

		String pathToDirectory = getDirectory();

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

	public void processCommandLine(String [] args) {
		JCommander cmd = new JCommander(this);
		cmd.parse(args);
		cmd.setProgramName(COMMAND_NAME);

		if(passPhrase == null) {
			log("Command line error: Pass phrase is a mandatory parameter, see usage below");
			cmd.usage();
			System.exit(1);
		}
		else if(help) {
			cmd.usage();
			System.exit(0);
		}
	}

	public Wallet createWallet() {
		Protocol protocol = getProtocol();
		List<String> mnemonicWords = getMnemonicWords(protocol);

		return protocol.createWallet(mnemonicWords, passPhrase);
	}

	public Wallet restoreWallet(File file) {
		try {
			JSONObject walletJson = FileUtility.readJsonFile(file);
			Protocol protocol = ProtocolFactory.getInstance(walletJson);
			wallet = protocol.restoreWallet(walletJson, passPhrase);
		}
		catch(Exception e) {
			throw new VerifyWalletFileException(e);
		}
		
		return wallet;
	}

	public File writeWalletFile(Wallet wallet, String path) {
		String fileContent = wallet.toString();
		String fileName = getWalletFileName(wallet, path);
		return FileUtility.saveToFile(fileContent, fileName);
	}

	public Wallet getWallet() {
		return wallet;
	}

	public boolean isCreateMode() {
		return walletFile == null;
	}

	/**
	 * Returns the wallet file directory using the following fallback mechanism.
	 * (1) If a wallet file was provided on the command line, its directory is returned.
	 * (2) If a directory was provided return that directory.
	 * (3) Else: return the default directory.
	 */
	public String getDirectory() {
		if(!isCreateMode()) {
			File file = getWalletFile();
			String parent = file == null ? null : file.getParent();
			return parent != null ? parent : ".";
		}
		else {
			return targetDirectory;
		}
	}

	/**
	 * Returns the wallet file specified on the command line or null if no wallet file to verify was specified.
	 * @return
	 */
	public File getWalletFile() {
		return walletFile == null ? null : new File(walletFile);
	}

	public Protocol getProtocol() {
		return ProtocolFactory.getInstance(Technology.get(technology), Network.get(network));
	}

	private List<String> getMnemonicWords(Protocol protocol) {
		return mnemonic != null ? Mnemonic.convert(mnemonic) : protocol.generateMnemonicWords();
	}

	private void writeFiles(Wallet wallet, String pathToDirectory) {
		log("writing wallet file ...");
		writeWalletFile(wallet, pathToDirectory);

		log("writing html and png output files ...");
		writeHtmlFile(wallet, pathToDirectory);
		writeQRCodeFile(wallet, pathToDirectory);

		log(String.format("files successfully written to directory", pathToDirectory));
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

	private String getWalletFileName(Wallet wallet, String path) {
		return getFileName(wallet, path, EXT_JSON);
	}

	private String getFileName(Wallet wallet, String path, String extension) {
		return String.format("%s%s%s.%s", path, File.separator, wallet.getBaseName(), extension);
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
