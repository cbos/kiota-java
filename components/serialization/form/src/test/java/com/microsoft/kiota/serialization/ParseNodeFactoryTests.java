package com.microsoft.kiota.serialization;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;

class ParseNodeFactoryTests {
	private static final FormParseNodeFactory _parseNodeFactory = new FormParseNodeFactory();
	private static final String contentType = "application/x-www-form-urlencoded";
	@Test
	void getsWriterForFormContentType() throws UnsupportedEncodingException {
		final var initialString = "key1=value1&key2=value2";
		final var rawResponse = new ByteArrayInputStream(initialString.getBytes("UTF-8"));
		final var parseNode = _parseNodeFactory.getParseNode(contentType, rawResponse);
		assertNotNull(parseNode);
	}
	@Test
	void throwsArgumentOutOfRangeExceptionForInvalidContentType() throws UnsupportedEncodingException {
		final var initialString = "key1=value1&key2=value2";
		final var rawResponse = new ByteArrayInputStream(initialString.getBytes("UTF-8"));
		assertThrows(IllegalArgumentException.class, () -> _parseNodeFactory.getParseNode("application/json", rawResponse));
	}
	@Test
	void throwsArgumentNullExceptionForNoContentType() throws UnsupportedEncodingException {
		final var initialString = "key1=value1&key2=value2";
		final var rawResponse = new ByteArrayInputStream(initialString.getBytes("UTF-8"));
		assertThrows(NullPointerException.class, () -> _parseNodeFactory.getParseNode("", rawResponse));
	}
}
