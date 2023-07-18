package com.microsoft.kiota.serialization;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SerializationWriterFactoryTests {
	private static final FormSerializationWriterFactory _serializationWriterFactory = new FormSerializationWriterFactory();
	private static final String contentType = "application/x-www-form-urlencoded";
	@Test
	void getsWriterForFormContentType() {
		final var serializationWriter = _serializationWriterFactory.getSerializationWriter(contentType);
		assertNotNull(serializationWriter);
	}
	@Test
	void throwsArgumentOutOfRangeExceptionForInvalidContentType() {
		assertThrows(IllegalArgumentException.class, () -> _serializationWriterFactory.getSerializationWriter("application/json"));
	}
	@Test
	void throwsArgumentNullExceptionForNoContentType() {
		assertThrows(NullPointerException.class, () -> _serializationWriterFactory.getSerializationWriter(""));
	}
}
