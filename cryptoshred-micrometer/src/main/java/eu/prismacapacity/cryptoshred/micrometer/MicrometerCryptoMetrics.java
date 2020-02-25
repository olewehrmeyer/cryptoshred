/*
 * Copyright © 2020 PRISMA European Capacity Platform GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.prismacapacity.cryptoshred.micrometer;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import eu.prismacapacity.cryptoshred.core.metrics.CryptoMetrics;
import eu.prismacapacity.cryptoshred.core.metrics.MetricsCallable;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MicrometerCryptoMetrics implements CryptoMetrics {

	private final MeterRegistry reg = Metrics.globalRegistry;
	@NonNull
	private final Counter missingKey;
	@NonNull
	private final Counter decryptionSuccess;
	@NonNull
	private final Counter decryptionFailure;
	@NonNull
	private final Counter keyLookUp;
	@NonNull
	private final Counter keyCreation;
	@NonNull
	private final Counter keyCreationAfterConflict;

	public MicrometerCryptoMetrics() {
		missingKey = reg.counter("cryptoshred_missing_key");
		decryptionSuccess = reg.counter("cryptoshred_decryption_success");
		decryptionFailure = reg.counter("cryptoshred_decryption_failure");
		keyLookUp = reg.counter("cryptoshred_key_lookup");
		keyCreation = reg.counter("cryptoshred_key_creation");
		keyCreationAfterConflict = reg.counter("cryptoshred_key_creation_after_conflict");
	}

	@Override
	public void notifyMissingKey() {
		missingKey.increment();
	}

	@Override
	public void notifyDecryptionSuccess() {
		decryptionSuccess.increment();
	}

	@Override
	public void notifyDecryptionFailure(Exception e) {
		decryptionFailure.increment();
	}

	@Override
	public void notifyKeyLookUp() {
		keyLookUp.increment();
	}

	@Override
	public void notifyKeyCreation() {
		keyCreation.increment();
	}

	private <T> T timed(String timerName, MetricsCallable<T> fn) {
		return reg.timer(timerName).record(fn::call);
	}

	@Override
	public <T> T timedCreateKey(MetricsCallable<T> fn) {
		return timed("cryptoshred_create_key_in_dynamodb_table", fn);
	}

	@Override
	public <T> T timedFindKey(MetricsCallable<T> fn) {
		return timed("cryptoshred_find_key_in_dynamodb_table", fn);
	}

	@Override
	public void notifyKeyCreationAfterConflict() {
		keyCreationAfterConflict.increment();
	}

}
