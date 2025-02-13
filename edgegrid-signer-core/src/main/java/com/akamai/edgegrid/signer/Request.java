package com.akamai.edgegrid.signer;

import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Library-agnostic representation of an HTTP request. This object is immutable, so you probably
 * want to build an instance using {@link RequestBuilder}. Extenders of
 * {@link AbstractEdgeGridRequestSigner} will need to build one of these as part of their
 * implementation.
 *
 */
public class Request implements Comparable<Request> {

    /** A {@link String} {@link Comparator}. */
    private static Comparator<String> stringComparator = new NullSafeComparator<>();

    /** A {@link URI} {@link Comparator}. */
    private static Comparator<URI> uriComparator = new NullSafeComparator<>();

    private final byte[] body;
    private final String method;
    private final URI uri;
    private final Map<String, String> headers;

    /**
     * Creates a new instance of {@link Request} using provided request builder.
     *
     * @param b {@link RequestBuilder}
     */
    private Request(RequestBuilder b) {
        this.body = b.body;
        this.method = b.method;
        this.headers = b.headers;
        this.uri = b.uri;
    }

    /**
     * Returns a new builder. The returned builder is equivalent to the builder
     * generated by {@link RequestBuilder}.
     *
     * @return a fresh {@link RequestBuilder}
     */
    public static RequestBuilder builder() {
        return new RequestBuilder();
    }

    @Override
    public int compareTo(Request that) {
        if (that == null) {
            return 1;
        }
        int comparison = 0;
        comparison = uriComparator.compare(this.uri, that.uri);
        if (comparison == 0) {
            comparison = stringComparator.compare(this.method, that.method);
        }
        if (comparison == 0) {
            comparison = Integer.compare(this.body.length, that.body.length);
        }
        if (comparison == 0) {
            for (int i = 0; i < this.body.length && comparison == 0; i++) {
                byte left = this.body[i];
                byte right = that.body[i];
                comparison = left < right ? -1 : left > right ? 1 : 0;
            }
        }
        if (comparison == 0) {
            comparison = Integer.compare(this.headers.size(), that.headers.size());
        }
        if (comparison == 0) {
            for (String key : this.headers.keySet()) {
                if (!that.headers.containsKey(key)) {
                    comparison = 1;
                    break;
                }
                comparison = this.headers.get(key).compareTo(that.headers.get(key));
                if (comparison != 0) {
                    break;
                }
            }
        }
        return comparison;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        final Request that = (Request) o;
        return compareTo(that) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, headers, method, uri);
    }

    @Override
    public String toString() {
        return new StringBuilder("[ ")
                .append("body: ").append(body).append("; ")
                .append("headers: ").append(headers).append("; ")
                .append("method: ").append(method).append("; ")
                .append("uri: ").append(uri)
                .append(" ]")
                .toString();
    }

    byte[] getBody() {
        return body;
    }

    Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    String getMethod() {
        return method;
    }

    URI getUri() {
        return uri;
    }

    /**
     * Creates an instance of {@link Request#builder()}. The returned builder is equivalent to the builder
     * generated by {@link Request#builder()}.
     */
    public static class RequestBuilder {

        private byte[] body = new byte[]{};
        private Map<String, String> headers = new HashMap<>();
        private String method;
        private URI uri;

        /**
         * Sets a content of HTTP request body. If not set, body is empty by default.
         *
         * @param requestBody a request body, in bytes
         * @return reference back to this builder instance
         */
        public RequestBuilder body(byte[] requestBody) {
            if (requestBody != null && requestBody.length != 0) {
                this.body = Arrays.copyOf(requestBody, requestBody.length);
            }
            return this;
        }

        /**
         * <p>
         * Adds a single header for an HTTP request. This can be called multiple times to add as
         * many headers as needed.
         * </p>
         * <p>
         * <i>NOTE: All header names are lower-cased for storage. In HTTP, header names are
         * case-insensitive anyway, and EdgeGrid does not support multiple headers with the same
         * name. Forcing to lowercase here improves our chance of detecting bad requests early.</i>
         * </p>
         *
         * @param headerName a header name
         * @param value a header value
         * @return reference back to this builder instance
         * @throws IllegalArgumentException if a duplicate header name is encountered
         */
        public RequestBuilder header(String headerName, String value) {
            if (headerName == null || "".equals(headerName)) {
                throw new IllegalArgumentException("headerName cannot be empty");
            }
            if (value == null || "".equals(value)) {
                throw new IllegalArgumentException("value cannot be empty");
            }
            headerName = headerName.toLowerCase();
            if (this.headers.containsKey(headerName)) {
                throw new IllegalArgumentException("Duplicate header found: " + headerName);
            }
            headers.put(headerName, value);
            return this;
        }

        /**
         * <p>
         * Sets headers of HTTP request. The {@code headers} parameter is copied so that changes
         * to the original {@link Map} will not impact the stored reference.
         * </p>
         * <p>
         * <i>NOTE: All header names are lower-cased for storage. In HTTP, header names are
         * case-insensitive anyway, and EdgeGrid does not support multiple headers with the same
         * name. Forcing to lowercase here improves our chance of detecting bad requests early.</i>
         * </p>
         *
         * @param headers a {@link Map} of headers
         * @return reference back to this builder instance
         * @throws IllegalArgumentException if a duplicate header name is encountered
         */
        public RequestBuilder headers(Map<String, String> headers)  {
            Objects.requireNonNull(headers, "headers cannot be null");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
            return this;
        }

        /**
         * Sets HTTP method: GET, PUT, POST, DELETE. Mandatory to set.
         *
         * @param method an HTTP method
         * @return reference back to this builder instance
         */
        public RequestBuilder method(String method) {
            if (Objects.isNull(method) || "".equals(method)) {
                throw new IllegalArgumentException("method cannot be empty");
            }
            this.method = method;
            return this;
        }

        /**
         * <p>
         * Sets the URI of the HTTP request. This URI <i>MUST</i> have the correct path and query
         * segments set. Scheme is assumed to be "HTTPS" for the purpose of this library. Host is
         * actually taken from a {@link ClientCredential} as signing time; any value in this URI is
         * discarded. Fragments are not signed.
         * </p>
         * <p>
         * A path and/or query string is required.
         * </p>
         *
         * @param uri a {@link URI}
         * @return reference back to this builder instance
         */
        public RequestBuilder uri(String uri) {
            if (uri == null || "".equals(uri)) {
                throw new IllegalArgumentException("uri cannot be empty");
            }
            return uri(URI.create(uri));
        }

        /**
         * <p>
         * Sets the URI of the HTTP request. This URI <i>MUST</i> have the correct path and query
         * segments set. Scheme is assumed to be "HTTPS" for the purpose of this library. Host is
         * actually taken from a {@link ClientCredential} as signing time; any value in this URI is
         * discarded. Fragments are not signed.
         * </p>
         * <p>
         * A path and/or query string is required.
         * </p>
         *
         * @param uri a {@link URI}
         * @return reference back to this builder instance
         */
        public RequestBuilder uri(URI uri) {
            Objects.requireNonNull(uri, "uri cannot be empty");
            try {
                this.uri = new URI(null, null, uri.getPath(), uri.getRawQuery(), null);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Error setting URI", e);
            }
            return this;
        }

        /**
         * <p>
         * Sets the URI of the HTTP request without any processing it, which is important when URI contains
         * path parameters which consists of encoded URLs.
         * This URI <i>MUST</i> have the correct path and query segments set. Scheme is assumed to be "HTTPS" for the purpose of this library. Host is
         * actually taken from a {@link ClientCredential} at signing time; any value in this URI is
         * discarded. Fragments are not removed from signing process.
         * </p>
         * <p>
         * A path and/or query string is required.
         * </p>
         *
         * @param uri a {@link URI}
         * @return reference back to this builder instance
         */
        public RequestBuilder rawUri(URI uri) {
            Objects.requireNonNull(uri, "uri cannot be empty");
            this.uri = uri;
            return this;
        }

        /**
         * Returns a newly-created immutable HTTP request.
         *
         * @return new HTTP request {@link Request}
         */
        public Request build() {
            Objects.requireNonNull(body, "body cannot be empty");
            Objects.requireNonNull(uri, "uriWithQuery cannot be empty");
            if (Objects.isNull(method) || "".equals(method)) {
                throw new IllegalArgumentException("method cannot be empty");
            }
            return new Request(this);
        }

    }

}
