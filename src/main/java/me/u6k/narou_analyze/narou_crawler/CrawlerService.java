
package me.u6k.narou_analyze.narou_crawler;

import java.net.MalformedURLException;
import java.net.URL;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndex;
import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.util.NetworkUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrawlerService {

    private static final Logger L = LoggerFactory.getLogger(CrawlerService.class);

    @Autowired
    private NovelIndexRepository repo;

    public void indexingNovel(String searchPageUrl) {
        try {
            URL url = new URL(searchPageUrl);
            String html = NetworkUtil.get(url);

            Document htmlDoc = Jsoup.parse(html);

            Elements novelLinks = htmlDoc.select("div.novel_h a.tl");
            if (novelLinks.size() == 0) {
                throw new RuntimeException("novelLinks.size == 0");
            }

            for (int i = 0; i < novelLinks.size(); i++) {
                Element novelLink = novelLinks.get(i);
                URL novelUrl = new URL(novelLink.attr("href"));
                String novelTitle = novelLink.text();
                L.info("novel: url={}, title={}", novelUrl, novelTitle);

                this.saveNovelIndex(novelUrl);
            }

            Elements nextLinks = htmlDoc.select("a.nextlink");
            if (nextLinks.size() > 0) {
                Element nextLink = nextLinks.get(0);
                String nextUrl = nextLink.attr("href");
                L.info("next: url={}", "http://yomou.syosetu.com/search.php" + nextUrl);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveNovelIndex(URL url) {
        String hash = DigestUtils.sha256Hex(url.toString());

        NovelIndex novelIndex = this.repo.findOne(hash);
        if (novelIndex != null) {
            return;
        }

        novelIndex = new NovelIndex();
        novelIndex.setId(hash);
        novelIndex.setUrl(url);
        this.repo.save(novelIndex);
    }

}
