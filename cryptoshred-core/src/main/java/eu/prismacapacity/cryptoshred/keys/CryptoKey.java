package eu.prismacapacity.cryptoshred.keys;

import java.util.Base64;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptoKey {
	@Getter
	@NonNull
	private final String base64;
	@Getter
	@NonNull
	private final byte[] bytes;

	public static CryptoKey fromBase64(@NonNull String base64encoded) {
		return new CryptoKey(base64encoded, Base64.getDecoder().decode(base64encoded));
	}

	public static CryptoKey fromBytes(@NonNull byte[] bytes) {
		return new CryptoKey(Base64.getEncoder().encodeToString(bytes), bytes);
	}

	@Override
	public String toString() {
		return getBase64();
	}

}