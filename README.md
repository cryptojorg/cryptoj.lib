# cryptoj.lib

## Purpose
Java Crypto Library for Bitcoin, Ethereum, Iota and more

The goal of this library is to provide a unified API to create and use paper wallets for different crypto currencies.

This repository also provides a sample application to demonstrate the usage of the provided API. So far the following currencies are supported 

* Bitcoin
* Ethereum
* Iota

## Warning/Current Status

This library and the demo application are currently experimental/educational only. 
No testing with real funds has taken place.

## Run the Application

After cloning this repo build the command line tool using Maven.

```
mvn clean package
```

The result of the Maven build is an executable JAR file.

### Creating Paper Wallets

Use the following command to create a paper wallet. For the example we will create a Bitcoin wallet.

```
java -jar target/cryptoj-lib-0.1.0-SNAPSHOT.jar -d . -p "test pass phrase" -t bitcoin```
```

This will lead to an output similar to the one below

```
writing wallet file ...
writing html and png output files ...
files successfully written to directory
wallet file: ./1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.json
protocol: Bitcoin (Production)
address: 1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1
encrypted: true
pass phrase: test pass phrase
seed: expose dwarf coyote broken alert rifle fade novel estate output about repair
```

Three files have been created

* A HTML file for printing (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.html)
* The actual wallet file (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.json)
* The corresponding address as QR code (1EQVGkJN6V8QdZ3ANsnXCHmhZRnnxL7Kx1.png)

An HTML output looks as shown  below.
![HTML Page](/screenshots/bitcoin_paper_wallet_html.png)

Using the created seed phrase "expose dwarf coyote broken alert rifle fade novel estate output about repair" may be used to restore the wallet with Electrum Bitcoin wallet.

![HTML Page](/screenshots/electrum_bitcoin_seed.png)

Using the Electrum wallet this leads to the same receive address as provided by the command line tool.

![HTML Page](/screenshots/electrum_receive_address.png)

