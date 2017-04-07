
package me.u6k.narou_analyze.narou_crawler.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public final class NetworkUtil {

    private NetworkUtil() {
    }

    public static String get(URL url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            Thread.sleep(10000);

            HttpGet get = new HttpGet(url.toURI());

            try (CloseableHttpResponse response = client.execute(get)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("url=" + url + ", statusCode=" + response.getStatusLine().getStatusCode());
                }

                byte[] responseData = EntityUtils.toByteArray(response.getEntity());

                String html = new String(responseData, StandardCharsets.UTF_8);

                return html;
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
