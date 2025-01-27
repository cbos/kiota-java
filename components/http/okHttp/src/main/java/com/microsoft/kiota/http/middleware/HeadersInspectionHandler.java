package com.microsoft.kiota.http.middleware;

import jakarta.annotation.Nonnull;
import kotlin.Pair;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import com.microsoft.kiota.http.middleware.options.HeadersInspectionOption;
import com.microsoft.kiota.http.middleware.options.RetryHandlerOption;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The middleware responsible for inspecting the request and response headers
 */
public class HeadersInspectionHandler implements Interceptor {
    /**
     * Create a new instance of the HeadersInspectionHandler class with the default options
     */
    public HeadersInspectionHandler() {
        this(new HeadersInspectionOption());
    }
    /**
     * Create a new instance of the HeadersInspectionHandler class with the provided options
     * @param options The options to use for the handler
     */
    public HeadersInspectionHandler(@Nonnull final HeadersInspectionOption options) {
        this.options = Objects.requireNonNull(options);
    }
    private final HeadersInspectionOption options;

    /** {@inheritDoc} */
    @Nonnull
    @Override
    @SuppressWarnings("UnknownNullness")
    public Response intercept(final Chain chain) throws IOException {
        Objects.requireNonNull(chain, "parameter chain cannot be null");
        Request request = chain.request();
        HeadersInspectionOption inspectionOption = request.tag(HeadersInspectionOption.class);
        if(inspectionOption == null) { inspectionOption = options; }
        final Span span = ObservabilityHelper.getSpanForRequest(request, "HeadersInspectionHandler_Intercept");
        Scope scope = null;
        if (span != null) {
            scope = span.makeCurrent();
            span.setAttribute("com.microsoft.kiota.handler.headersInspection.enable", true);
        }
        try {
            if (span != null) {
                request = request.newBuilder().tag(Span.class, span).build();
            }
            if (inspectionOption.getInspectRequestHeaders()) {
                for(final Pair<? extends String, ? extends String> header : request.headers()) {
                    inspectionOption.getRequestHeaders().put(header.getFirst(), Set.of(header.getSecond()));
                }
            }
            final Response response = chain.proceed(request);
            if (inspectionOption.getInspectResponseHeaders()) {
                for(final Pair<? extends String, ? extends String> header : response.headers()) {
                    inspectionOption.getResponseHeaders().put(header.getFirst(), Set.of(header.getSecond()));
                }
            }
            return response;
        } finally {
            if (scope != null) {
                scope.close();
            }
            if (span != null) {
                span.end();
            }
        }
    }
    
}
