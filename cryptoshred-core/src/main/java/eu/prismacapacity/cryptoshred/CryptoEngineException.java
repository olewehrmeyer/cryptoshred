package eu.prismacapacity.cryptoshred;

import lombok.NonNull;

public class CryptoEngineException extends RuntimeException {

	public CryptoEngineException(@NonNull Exception cause) {
		super(cause);
	}

	private static final long serialVersionUID = 1L;

}
