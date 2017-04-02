
package me.u6k.narou_analyze.narou_crawler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CrawlerService {

    private static final Logger L = LoggerFactory.getLogger(CrawlerService.class);

    public void indexingNovel(String searchPageUrl) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(searchPageUrl);

            try (CloseableHttpResponse response = client.execute(get)) {
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("url=" + searchPageUrl + ", statusCode=" + response.getStatusLine().getStatusCode());
                }

                byte[] responseData = EntityUtils.toByteArray(response.getEntity());

                String html = new String(responseData, StandardCharsets.UTF_8);
                L.info(html);

                Document htmlDoc = Jsoup.parse(html);

                Elements novelLinks = htmlDoc.select("div.novel_h a.tl");
                for (int i = 0; i < novelLinks.size(); i++) {
                    Element novelLink = novelLinks.get(i);
                    String novelUrl = novelLink.attr("href");
                    String novelTitle = novelLink.text();
                    L.info("novel: url={}, title={}", novelUrl, novelTitle);
                }

                Elements nextLinks = htmlDoc.select("a.nextlink");
                if (nextLinks.size() > 0) {
                    Element nextLink = nextLinks.get(0);
                    String nextUrl = nextLink.attr("href");
                    L.info("next: url={}", "http://yomou.syosetu.com/search.php" + nextUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
