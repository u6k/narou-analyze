
package me.u6k.narou_analyze.narou_crawler.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NetworkUtil {

    private static final Logger L = LoggerFactory.getLogger(NetworkUtil.class);

    private static final int INTERVAL = 1000;

    private NetworkUtil() {
    }

    public static String get(URL url) {
        return get(url, false);
    }

    public static String get(URL url, boolean gzip) {
        L.debug("#get: url={}", url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            Thread.sleep(INTERVAL);

            HttpGet get = new HttpGet(url.toURI());

            try (CloseableHttpResponse response = client.execute(get)) {
                L.debug("response: status={}", response.getStatusLine());
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("url=" + url + ", statusCode=" + response.getStatusLine().getStatusCode());
                }

                HttpEntity entity = response.getEntity();
                if (gzip) {
                    entity = new GzipDecompressingEntity(entity);
                }
                byte[] responseData = EntityUtils.toByteArray(entity);
                String html = new String(responseData, StandardCharsets.UTF_8);
                L.debug("response: body={}", html);

                return html;
            }
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
