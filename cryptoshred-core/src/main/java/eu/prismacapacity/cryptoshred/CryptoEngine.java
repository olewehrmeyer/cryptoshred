package eu.prismacapacity.cryptoshred;

import eu.prismacapacity.cryptoshred.keys.CryptoKey;
import eu.prismacapacity.cryptoshred.keys.CryptoKeySize;
import lombok.NonNull;

/**
 * Encapsulates the actual encryption / decryption using jdk or bouncy castle
 * 
 * @author uwe
 *
 */
public interface CryptoEngine {

	@NonNull
	byte[] decrypt(@NonNull CryptoAlgorithm algo, @NonNull CryptoKey cryptoKey, @NonNull byte[] bytes);

	@NonNull
	byte[] encrypt(@NonNull byte[] unencypted, @NonNull CryptoAlgorithm algorithm, @NonNull CryptoKey key,
			@NonNull CryptoObjectMapper mapper);

	@NonNull
	CryptoKey generateKey(@NonNull CryptoAlgorithm algo, @NonNull CryptoKeySize size);

}
