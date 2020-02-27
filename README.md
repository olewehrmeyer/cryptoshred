# cryptoshred

![Java CI](https://github.com/prisma-capacity/cryptoshred/workflows/Java%20CI/badge.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/333bfd567a6a447895212994b414f077)](https://app.codacy.com/gh/prisma-capacity/cryptoshred?utm_source=github.com&utm_medium=referral&utm_content=prisma-capacity/cryptoshred&utm_campaign=Badge_Grade_Settings)
[![codecov](https://codecov.io/gh/prisma-capacity/cryptoshred/branch/master/graph/badge.svg)](https://codecov.io/gh/prisma-capacity/cryptoshred)


### Motivation

Cryptoshredding is a well-known technique to 'erase' encryted data by throwing away the necessary key for decryption. 
Two of the advantages are

* You do not have to actually remove (or update) data, so that it can be immutable
* You don't have to keep track of where it is used, as long as it stays encrypted.

see https://en.wikipedia.org/wiki/Crypto-shredding

### cryptoshred library

In order to make use of that technique, you need to encrypt data before writing in the first place and maintain keys for the subjects. This is what cryptoshred can do for you.

#### Features

* Pluggable Metrics interface
  * optional Micrometer impl.
* Pluggable CryptoEngine
  * JDK-based impl.
* Pluggable Key Repository
  * optional Amazon DynamoDB impl.
* optional Spring-Boot autoconfiguration module
* optional Spring (not Boot) configuration module
* Jackson based deserialization to Java Objects
* Evolutionary approach to key definition creation and migration

### Encryption

In order to encrypt a particular piece of data, you use a 'CryptoContainerFactory' to wrap a CryptoContainer around the actual data before persisting.

```java
	@Data
	public class Person { // might be an Enitity, a POJO you serialize or anything you want to persist
		int age;
		CryptoContainer<String> name;
	}
```
Of course, in order to wrap the data (and encrypt it, you'd need to provide a 'CryptoSubjectId', that references the actual key, that you'd want to throw away in order to do the 'deletion' afterwards)

```java
		CryptoContainerFactory factory = ... // maybe injected from Spring or similar
		CryptoSubjectId id = CryptoSubjectId.of(UUID.randomUUID()); // simple value object

		Person p = new Person();
		p.name = factory.wrap("Peter", id);
		p.age = 30;

		// go persist the Person
```
A CryptoContainer consists of the following data:

* SubjectId
* Algorith used
* KeySize used
* Type of Object // String in this example
* Encoded byte array of the serialized form of the Object wrapped.

You can wrap any Jackson-serializable object like for instance

```java

		CryptoObjectMapper om = CryptoObjectMapper.builder(
			new InMemCryptoKeyRepository(engine), engine)
			.defaultKeySize(CryptoKeySize.of(256)) // optional
			.defaultAlgo(CryptoAlgorithm.AES_CBC)
			.build();


		CryptoContainerFactory factory = om; // maybe injected from Spring or similar
											 // actually implemented by CryptoObjectMapper

		CryptoSubjectId id = CryptoSubjectId.of(UUID.randomUUID()); // simple value object

		Person p = new Person();
		p.name = factory.wrap("Peter", id);
		p.age = 30;
		p.credicard = factory.wrap(new CreditCardInfo("12341234",CrediCardTypes.VISA));

		// go persist the Person
```

### Decryption

When using CryptoObjectMapper to deserialize a 'CryptoContainer', all necessary dependencies are injected into the Container automatically, so you can use the 'CryptoContainer' just like an Optional (it copied all the methods from 'java.util.Optional').

```java

		String json = om.writeValueAsString(p);

		Person p2 = om.readValue(json, Person.class);
		assertTrue(p2.name.isPresent());
		assertEquals("Peter",p2.name.get());

		// if you deleted the key in between, name.isPresent() would be false.
		// of course you should rather use p2.name.orElse("unknown") or something rather than get, but you know all that from 
```



