package org.cryptoj;

public class VerifyWalletFileException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public VerifyWalletFileException(Exception e) {
		super(e);
	}

    public VerifyWalletFileException(String message) {
        super(message);
    }
}
