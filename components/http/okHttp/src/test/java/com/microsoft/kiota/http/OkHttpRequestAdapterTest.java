package com.microsoft.kiota.http;

import static org.junit.jupiter.api.Assertions.*;
import io.opentelemetry.api.GlobalOpenTelemetry;
import okio.Okio;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.microsoft.kiota.authentication.AuthenticationProvider;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.ParseNodeFactory;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestInformation;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.stubbing.Answer;
import org.junit.jupiter.api.Test;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import com.microsoft.kiota.ApiException;

class OkHttpRequestAdapterTest {
	@ParameterizedTest
	@EnumSource(value = HttpMethod.class, names = {"PUT", "POST", "PATCH"})
	void PostRequestsShouldHaveEmptyBody(HttpMethod method) throws Exception { // Unexpected exception thrown: java.lang.IllegalArgumentException: method POST must have a request body.
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		final var adapter = new OkHttpRequestAdapter(authenticationProviderMock) {
			public Request test() throws Exception {
				var ri = new RequestInformation();
				ri.httpMethod = method;
				ri.urlTemplate = "http://localhost:1234";
				var span1 = GlobalOpenTelemetry.getTracer("").spanBuilder("").startSpan();
				var span2 = GlobalOpenTelemetry.getTracer("").spanBuilder("").startSpan();
				return this.getRequestFromRequestInformation(ri, span1, span2);
			}
		};

		final var request = assertDoesNotThrow(() -> adapter.test());
		assertNotNull(request.body());
	}
	@ParameterizedTest
	@ValueSource(ints = {200, 201, 202, 203, 206})
	void SendStreamReturnsUsableStream(int statusCode) throws Exception {
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		when(authenticationProviderMock.authenticateRequest(any(RequestInformation.class), any(Map.class))).thenReturn(CompletableFuture.completedFuture(null));
		final var text = "my-demo-text";
		final var bufferedSource = Okio.buffer(Okio.source(new ByteArrayInputStream(text.getBytes("UTF-8"))));
		final var client = getMockClient(new Response.Builder()
													.code(statusCode)
													.message("OK")
													.protocol(Protocol.HTTP_1_1)
													.request(new Request.Builder().url("http://localhost").build())
													.body(ResponseBody.create(bufferedSource, MediaType.parse("application/binary"), text.getBytes("UTF-8").length))
													.build());
		final var requestAdapter = new OkHttpRequestAdapter(authenticationProviderMock, null, null, client);
		final var requestInformation = new RequestInformation() {{
			setUri(new URI("https://localhost"));
			httpMethod = HttpMethod.GET;
		}};
		InputStream response = null;
		try {
			response = requestAdapter.sendPrimitiveAsync(requestInformation, InputStream.class, null).get();
			assertNotNull(response);
			assertEquals(text, new String(response.readAllBytes(), StandardCharsets.UTF_8));
		} finally {
			if (response != null) {
				response.close();	
			}
		}
	}
	@ParameterizedTest
	@ValueSource(ints = {200, 201, 202, 203, 204})
	void SendStreamReturnsNullOnNoContent(int statusCode) throws Exception {
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		when(authenticationProviderMock.authenticateRequest(any(RequestInformation.class), any(Map.class))).thenReturn(CompletableFuture.completedFuture(null));
		final var client = getMockClient(new Response.Builder()
													.code(statusCode)
													.message("OK")
													.protocol(Protocol.HTTP_1_1)
													.request(new Request.Builder().url("http://localhost").build())
													.body(null)
													.build());
		final var requestAdapter = new OkHttpRequestAdapter(authenticationProviderMock, null, null, client);
		final var requestInformation = new RequestInformation() {{
			setUri(new URI("https://localhost"));
			httpMethod = HttpMethod.GET;
		}};
		final var response = requestAdapter.sendPrimitiveAsync(requestInformation, InputStream.class, null).get();
		assertNull(response);
	}
	@ParameterizedTest
	@ValueSource(ints = {200, 201, 202, 203, 204, 205})
	void SendReturnsNullOnNoContent(int statusCode) throws Exception {
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		when(authenticationProviderMock.authenticateRequest(any(RequestInformation.class), any(Map.class))).thenReturn(CompletableFuture.completedFuture(null));
		final var client = getMockClient(new Response.Builder()
													.code(statusCode)
													.message("OK")
													.protocol(Protocol.HTTP_1_1)
													.request(new Request.Builder().url("http://localhost").build())
													.body(null)
													.build());
		final var requestAdapter = new OkHttpRequestAdapter(authenticationProviderMock, null, null, client);
		final var requestInformation = new RequestInformation() {{
			setUri(new URI("https://localhost"));
			httpMethod = HttpMethod.GET;
		}};
		final var mockEntity = mock(Parsable.class);
		when(mockEntity.getFieldDeserializers()).thenReturn(new HashMap<>());
		final var response = requestAdapter.sendAsync(requestInformation, (node) -> mockEntity, null).get();
		assertNull(response);
	}
	@ParameterizedTest
	@ValueSource(ints = {200, 201, 202, 203})
	void SendReturnsObjectOnContent(int statusCode) throws Exception {
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		when(authenticationProviderMock.authenticateRequest(any(RequestInformation.class), any(Map.class))).thenReturn(CompletableFuture.completedFuture(null));
		final var client = getMockClient(new Response.Builder()
													.code(statusCode)
													.message("OK")
													.protocol(Protocol.HTTP_1_1)
													.request(new Request.Builder().url("http://localhost").build())
													.body(ResponseBody.create("test".getBytes("UTF-8"), MediaType.parse("application/json")))
													.build());
		final var requestInformation = new RequestInformation() {{
			setUri(new URI("https://localhost"));
			httpMethod = HttpMethod.GET;
		}};
		final var mockEntity = mock(Parsable.class);
		when(mockEntity.getFieldDeserializers()).thenReturn(new HashMap<>());
		final var mockParseNode = mock(ParseNode.class);
		when(mockParseNode.getObjectValue(any(ParsableFactory.class))).thenReturn(mockEntity);
		final var mockFactory = mock(ParseNodeFactory.class);
		when(mockFactory.getParseNode(any(String.class), any(InputStream.class))).thenReturn(mockParseNode);
		when(mockFactory.getValidContentType()).thenReturn("application/json");
		final var requestAdapter = new OkHttpRequestAdapter(authenticationProviderMock, mockFactory, null, client);
		final var response = requestAdapter.sendAsync(requestInformation, (node) -> mockEntity, null).get();
		assertNotNull(response);
	}
	@Test
	void throwsAPIException() throws Exception  {
		final var authenticationProviderMock = mock(AuthenticationProvider.class);
		when(authenticationProviderMock.authenticateRequest(any(RequestInformation.class), any(Map.class))).thenReturn(CompletableFuture.completedFuture(null));
		final var client = getMockClient(new Response.Builder()
													.code(404)
													.message("Not Found")
													.protocol(Protocol.HTTP_1_1)
													.request(new Request.Builder().url("http://localhost").build())
													.body(ResponseBody.create("test".getBytes("UTF-8"), MediaType.parse("application/json")))
													.header("request-id", "request-id-value")
													.build());
		final var requestInformation = new RequestInformation() {{
			setUri(new URI("https://localhost"));
			httpMethod = HttpMethod.GET;
		}};
		final var mockEntity = mock(Parsable.class);
		when(mockEntity.getFieldDeserializers()).thenReturn(new HashMap<>());
		final var mockParseNode = mock(ParseNode.class);
		when(mockParseNode.getObjectValue(any(ParsableFactory.class))).thenReturn(mockEntity);
		final var mockFactory = mock(ParseNodeFactory.class);
		when(mockFactory.getParseNode(any(String.class), any(InputStream.class))).thenReturn(mockParseNode);
		when(mockFactory.getValidContentType()).thenReturn("application/json");
		final var requestAdapter = new OkHttpRequestAdapter(authenticationProviderMock, mockFactory, null, client);
		final var exception = assertThrows(ExecutionException.class, ()->requestAdapter.sendAsync(requestInformation, (node) -> mockEntity, null).get()) ;
		final var cause = exception.getCause();	
		assertTrue(cause instanceof ApiException);
		assertEquals(404, ((ApiException)cause).responseStatusCode);
		assertTrue(((ApiException)cause).getResponseHeaders().containsKey("request-id"));
	}
	public static OkHttpClient getMockClient(final Response response) throws IOException {
        final OkHttpClient mockClient = mock(OkHttpClient.class);
        final Call remoteCall = mock(Call.class);
        final Dispatcher dispatcher = new Dispatcher();
        when(remoteCall.execute()).thenReturn(response);
        doAnswer((Answer<Void>) invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onResponse(null, response);
            return null;
        }).when(remoteCall)
            .enqueue(any(Callback.class));
        when(mockClient.dispatcher()).thenReturn(dispatcher);
        when(mockClient.newCall(any())).thenReturn(remoteCall);
        return mockClient;
    }
}
