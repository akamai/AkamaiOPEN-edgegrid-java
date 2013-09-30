edgegrid-auth-java
==================

Java library for EdgeGrid Client Authentication

==SUMMARY

edgegrid-auth-java is a Java library for signing requests to APIs that are carried on the Akamai EdgeGrid network. It builds on the [Google HTTP Client Library for Java](https://code.google.com/p/google-http-java-client/) and adds the EdgeGrid signature to a normal [HttpRequest](http://javadoc.google-http-java-client.googlecode.com/hg/1.17.0-rc/com/google/api/client/http/HttpRequest.html).


==USAGE

Use the library is pretty simple.

First, create a *RequestSigner* object. *EdgeGridV1Signer* is one (the only currently provided) implementation of the *RequestSigner* interface.

The constructor of *EdgeGridV1Signer* takes two parameters:

* *headers*: for specifying the ordered list of request headers to be included in the request signature. This is provided by the API service provider.
* *maxBodySize*: for specifying the maximum allowed size in bytes of the request body, for POST and PUT requests. This value is also provided by the API service provider.

This *RequestSigner* object can then be used to sign the requests.

To sign an [HttpRequest](http://javadoc.google-http-java-client.googlecode.com/hg/1.17.0-rc/com/google/api/client/http/HttpRequest.html):

1. first, add the Host header with the hostname of the request;

2. then, sign the request with a *ClientCredential* that encapsulates the following:

 * *clientToken*: for specifying the client token obtained from the client provisioning process
 * *accessToken*: for specifying the access token obtained from the client authorization process
 * *clientSecret*: for specifying the client secret that is associated with the client token


==EXAMPLE

Here is an example code snippet:

		...         
		RequestSigner signer = new EdgeGridV1Signer(Collections.EMPTY_LIST, 1024 * 2);         
        URI uri = new URI("https", "akaa-u5x3btzf44hplb4q-6jrzwnvo7llch3po.luna.akamaiapis.net", "/billing-usage/v1/reportSources", null, null);         
		HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();        
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();        
		HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));        
		HttpHeaders headers = request.getHeaders();        
		headers.set("Host", "akaa-3fz725qlrtpbyl3k-c7tzr6rinu3vb2b7.luna-staging.akamaiapis.net");        
		ClientCredential credential = new DefaultCredential("akaa-nev5k66unzize2gx-5uz4svbszp4ko5wq",
			"akaa-ublu6mqdcqkjw5lz-542a56pcogddddow",
			"SOMESECRET");         
		HttpRequest signedRequest = signer.sign(request, credential);
		...         
