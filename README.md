# cryptoj.lib

The goal of this Java library is to create paper wallets for different crypto currencies.

## Warning/Current Status

The cryptoj library and the demo application are currently experimental/educational only. 
No testing with real funds has taken place.

## Main Features

* Paper wallets (cold storage)
* Multi-protocol (Bitcoin, Ethereum, ...)
* Compliant with widely used Wallets (Electrum, Metamask)
* Unified API
* Open Source

This repository also provides a sample application to demonstrate the usage of the provided API. So far the following currencies are supported 

* Bitcoin
* Ethereum

## Technical Backgound

Behind the scences the library creates hierarchical deterministic wallets from mnemonic seed phrases. 
For this the following standards are used.

* [BIP39](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki) Mnemonic code for generating deterministic keys 
* [BIP32](https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki) Hierarchical Deterministic Wallets 
* [BIP44](https://github.com/bitcoin/bips/blob/master/bip-0044.mediawiki) Multi-Account Hierarchy for Deterministic Wallets

The implementation of the library relies on widely used Java libraries for Bitcoin and Ethereum:

* [bitcoinj](https://github.com/bitcoinj/bitcoinj)
* [web3j](https://github.com/web3j/web3j)

## Building the Library/Sample Application

After cloning this repo build the command line tool using Maven.

```bash
mvn clean package
```

The result of the Maven build is an executable JAR file.

### Creating Bitcoin Paper Wallets

Use the following command to create a Bitcoin paper wallet. 

```bash
java -jar target/cryptoj-lib-0.2.0-SNAPSHOT.jar \
-pp "test pass phrase" \
-m "expose dwarf coyote broken alert rifle fade novel estate output about repair" \
-p Bitcoin
```

This will lead to an output similar to the one below

```
writing wallet file ...
writing html and png output files ...
files successfully written to directory
wallet file: ./1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.json
protocol: Bitcoin (Production)
wallet app: Electrum
address: 1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1
mnemonic: expose dwarf coyote broken alert rifle fade novel estate output about repair
pass phrase: test pass phrase
encrypted: true
```

Three files have been created

* A HTML file for printing (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.html)
* The actual wallet file (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.json)
* The corresponding address as QR code (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.png)

An HTML output looks as shown  below.
![HTML Page](/screenshots/bitcoin_paper_wallet.png)

Using the created seed phrase "expose dwarf coyote broken alert rifle fade novel estate output about repair" may be used to restore the wallet with [Electrum Bitcoin wallet](https://electrum.org/).

![Electrum Bitcoin Seed](/screenshots/electrum_bitcoin_seed.png)

Using the Electrum wallet this leads to the same receive address as provided by the command line tool.

![Electrum Receive Address](/screenshots/electrum_receive_address.png)


### Creating Ethereum Paper Wallets

Use the following command to create a Ethereum paper wallet. 

```bash
java -jar target/cryptoj-lib-0.2.0-SNAPSHOT.jar \
-pp "test pass phrase" \
-m "expose dwarf coyote broken alert rifle fade novel estate output about repair" \
-p Ethereum
```

This will lead to an output similar to the one below

```
writing wallet file ...
writing html and png output files ...
files successfully written to directory
wallet file: ./0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f.json
protocol: Ethereum (Production)
wallet app: MetaMask
address: 0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f
mnemonic: expose dwarf coyote broken alert rifle fade novel estate output about repair
pass phrase: test pass phrase
encrypted: true
```

Three files have been created

* A HTML file for printing (0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f.html)
* The actual wallet file (0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f.json)
* The corresponding address as QR code (0xF2E12BCFE9CF398b24492Df9fc02Af3397ED719f.png)

An HTML output looks as shown  below.
![HTML Page](/screenshots/ethereum_paper_wallet.png)

Using the created seed phrase "expose dwarf coyote broken alert rifle fade novel estate output about repair" may be used to restore the wallet with [MetaMask wallet](https://metamask.io/).

![MetaMask account restoration](/screenshots/metamask_restore.png)

Using the Electrum wallet this leads to the same receive address as provided by the command line tool.

![MetaMask Account](/screenshots/metamask_address.png)

