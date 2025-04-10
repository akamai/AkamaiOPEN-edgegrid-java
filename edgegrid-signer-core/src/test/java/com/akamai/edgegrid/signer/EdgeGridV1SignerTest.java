package com.akamai.edgegrid.signer;

import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests EdgeGridV1Signer.
 *
 */
public class EdgeGridV1SignerTest {

    private static Object[][] combine(Object[][]... testData) {
        List<Object[]> result = new ArrayList<>();
        for (Object[][] td : testData) {
            result.addAll(Arrays.asList(td));
        }
        return result.toArray(new Object[result.size()][]);
    }

    private static String repeat(char ch, int length) {
        char[] chars = new char[length];
        Arrays.fill(chars, ch);
        return new String(chars);
    }

    @Test(dataProvider = "testData")
    public void test(String caseName,
                     Request request,
                     ClientCredential clientCredential, long timestamp, String nonce,
                     String expectedAuthorizationHeader) throws RequestSigningException {
        String actualAuthorizationHeader = new EdgeGridV1Signer().getSignature(request, clientCredential, timestamp, nonce);
        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @DataProvider
    public Object[][] testData() throws RequestSigningException, URISyntaxException {
        return combine(
                basicTests(),
                pythonCases());
    }

    public Object[][] basicTests() throws URISyntaxException {
        ClientCredential clientCredential = ClientCredential.builder()
                .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234")
                .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyTRKhGxms=")
                .clientToken("akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj")
                .host("control.akamai.com")
                .build();
        String fixedNonce = "ec9d20ee-1e9b-4c1f-925a-f0017754f86c";
        // Fixed timestamp corresponds to 2016-08-04T07:00:00+0000.
        long fixedTimestamp = 1470294000000L;

        return new Object[][]{
                {"GET request",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("https://any-hostname-at-all.com/check"))
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=8GpKbZnIx4XEw/zXtQdbVwIu0zJSG0RpNiVTSyIUwr0=",
                },
                {"GET request with query",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("https://control.akamai.com/check?maciek=value"))
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=x10Wq9yA03bt+1nvPJPgVdReeIp91yLWjR0UPDSbL1Q=",
                },
                {"POST request",
                        Request.builder()
                                .method("POST")
                                .uri(URI.create("https://any-hostname-at-all.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=fN+xqlaSh0P07vBQ5cSCNK8gCYJfFIltzl6xrTjC6i0=",
                },
                {"For PUT request we ignore body",
                        Request.builder()
                                .method("PUT")
                                .uri(URI.create("https://control.akamai.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=OFMfMK4ROsW+TTmauMOjyrFuBr7jcJ1b6sb0+jIYj24=",
                },
                {"GET without scheme or hostname",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/check"))
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=8GpKbZnIx4XEw/zXtQdbVwIu0zJSG0RpNiVTSyIUwr0=",
                },
                {"GET with query string containing escaped characters",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/api/v1/data?query=%7B%22foo%22%3A%22bar%22%7D"))
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=qNVm6x91VcPKfUqCOeqvEXvNkqfD9kNnj3qVXYIWQyA=",
                },
                {"GET with path containing escaped characters",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/api/v1/foo%20resource"))
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;" +
                                "access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;" +
                                "timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=cXz7CtWUEi1WvNhNDYrcveufmfXBQc/yqO/JhHNloi4=",
                },
        };
    }



    /**
     * Cases taken from https://github.com/akamai-open/AkamaiOPEN-edgegrid-python/blob/master/akamai/edgegrid/test/testdata.json
     */
    public Object[][] pythonCases() throws RequestSigningException {

        int maxSize = 2048;
        ClientCredential clientCredential = ClientCredential.builder()
                .host("akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                .accessToken("akab-access-token-xxx-xxxxxxxxxxxxxxxx")
                .clientToken("akab-client-token-xxx-xxxxxxxxxxxxxxxx")
                .clientSecret("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx=")
                .maxBodySize(maxSize)
                .headerToSign("X-Test1")
                .headerToSign("X-Test2")
                .headerToSign("X-Test3")
                .build();
        String fixedNonce = "nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
        // Fixed timestamp corresponds to 20140321T19:34:21+0000
        long fixedTimestamp = 1395430461000L;
        return new Object[][]{

                {"simple GET",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=tL+y4hxyHxgWVD30X3pWnGKHcPzmrIF+LThiAOhMxYU="},

                {"GET with querystring",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/testapi/v1/t1?p1=1&p2=2"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=hKDH1UlnQySSHjvIcZpDMbQHihTQ0XyVAKZaApabdeA="},

                {"POST inside limit",
                        Request.builder()
                                .method("POST")
                                .uri(URI.create("/testapi/v1/t3"))
                                .body("datadatadatadatadatadatadatadata".getBytes())
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=hXm4iCxtpN22m4cbZb4lVLW5rhX8Ca82vCFqXzSTPe4="},

                {"POST too large",
                        Request.builder()
                                .method("POST")
                                .uri(URI.create("/testapi/v1/t3"))
                                .body(repeat('d', maxSize + 1).getBytes())
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=6Q6PiTipLae6n4GsSIDTCJ54bEbHUBp+4MUXrbQCBoY="},

                {"POST too large",
                        Request.builder()
                                .method("POST")
                                .uri(URI.create("/testapi/v1/t3"))
                                .body(repeat('d', maxSize).getBytes())
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=6Q6PiTipLae6n4GsSIDTCJ54bEbHUBp+4MUXrbQCBoY="},

                {"POST empty body",
                        Request.builder()
                                .method("POST")
                                .uri(URI.create("/testapi/v1/t6"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .body("".getBytes())
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=1gEDxeQGD5GovIkJJGcBaKnZ+VaPtrc4qBUHixjsPCQ="},

                {"Simple header signing with GET",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/testapi/v1/t4"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .header("X-Test1", "test-simple-header")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=8F9AybcRw+PLxnvT+H0JRkjROrrUgsxJTnRXMzqvcwY="},

                {"Header with leading and interior spaces",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/testapi/v1/t4"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .header("X-Test1", "     first-thing      second-thing")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=WtnneL539UadAAOJwnsXvPqT4Kt6z7HMgBEwAFpt3+c="},

                {"Headers out of order",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/testapi/v1/t4"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .header("X-Test2", "t2")
                                .header("X-Test1", "t1")
                                .header("X-Test3", "t3")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=Wus73Nx8jOYM+kkBFF2q8D1EATRIMr0WLWwpLBgkBqY="},

                {"Extra header",
                        Request.builder()
                                .method("GET")
                                .uri(URI.create("/testapi/v1/t5"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .header("X-Test2", "t2")
                                .header("X-Test1", "t1")
                                .header("X-Test3", "t3")
                                .header("X-Extra", "this won't be included")
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=Knd/jc0A5Ghhizjayr0AUUvl2MZjBpS3FDSzvtq4Ixc="},

                {"PUT test",
                        Request.builder()
                                .method("PUT")
                                .uri(URI.create("/testapi/v1/t6"))
                                .header("Host", "akaa-baseurl-xxxxxxxxxxx-xxxxxxxxxxxxx.luna.akamaiapis.net")
                                .body("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP".getBytes())
                                .build(),
                        clientCredential, fixedTimestamp, fixedNonce,
                        "EG1-HMAC-SHA256 client_token=akab-client-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "access_token=akab-access-token-xxx-xxxxxxxxxxxxxxxx;" +
                                "timestamp=20140321T19:34:21+0000;" +
                                "nonce=nonce-xx-xxxx-xxxx-xxxx-xxxxxxxxxxxx;signature=GNBWEYSEWOLtu+7dD52da2C39aX/Jchpon3K/AmBqBU="}

        };
    }
}
