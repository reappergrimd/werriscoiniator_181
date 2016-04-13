package com.nitinsurana.bitcoinlitecoin.rpcconnector.events;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import com.nitinsurana.bitcoinlitecoin.rpcconnector.RPCApp;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.exception.CryptoCurrencyRpcException;
import com.nitinsurana.bitcoinlitecoin.rpcconnector.pojo.Transaction;

/**
 * The class allows you to subscribe to the events associated with purses.
 * Bitcoind must be configured with -walletnotify parameter.
 */
public class WalletListener extends Observable implements Observer {
	public static final Logger LOG = Logger.getLogger("rpcLogger");

	final private Observable walletListener;
	final private RPCApp client;
	public Thread listener = null;

	public WalletListener(final RPCApp client, int port) throws IOException {
		walletListener = new BitcoinDListener(port);
		this.client = client;
	}

	@Override
	public synchronized void addObserver(Observer o) {
		if (listener == null) {
			walletListener.addObserver(this);
			listener = new Thread((Runnable) walletListener, "walletListener");
			listener.start();
		}
		super.addObserver(o);
	}

	
	public void update(Observable o, Object arg) {
		final String value = ((String) arg).trim();
		new Thread() {
			public void run() {
				try {
					Transaction tx = client.getTransaction(value);
					LOG.info("WalletEvent. TxId: " + tx.getTxid() + " Amount: " + tx.getAmount());
					setChanged();
					notifyObservers(tx);
				} catch (CryptoCurrencyRpcException e) {
					e.printStackTrace();
				}

			}
		}.start();
	}
	
	public void stop(){
		listener.interrupt();
	}

}
