
package me.u6k.narou_analyze.narou_crawler.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndex;
import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.util.NetworkUtil;
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

    public long indexingNovel(URL searchPageUrl) {
        try {
            long count = 0;

            while (searchPageUrl != null) {
                String html = NetworkUtil.get(searchPageUrl);

                Document htmlDoc = Jsoup.parse(html);

                Elements novelLinks = htmlDoc.select("div.novel_h a.tl");
                for (int i = 0; i < novelLinks.size(); i++) {
                    Element novelLink = novelLinks.get(i);
                    URL novelUrl = new URL(novelLink.attr("href"));
                    String novelTitle = novelLink.text();
                    L.debug("novel: url={}, title={}", novelUrl, novelTitle);

                    boolean saveResult = this.saveNovelIndex(novelUrl, novelTitle, new Date(0L));
                    if (saveResult) {
                        count++;
                    }
                }

                Elements nextLinks = htmlDoc.select("a.nextlink");
                if (nextLinks.size() > 0) {
                    Element nextLink = nextLinks.get(0);
                    searchPageUrl = new URL("http://yomou.syosetu.com/search.php" + nextLink.attr("href"));
                    L.debug("next: url={}", searchPageUrl);
                } else {
                    searchPageUrl = null;
                }
            }

            return count;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractNCode(URL url) {
        Pattern p = Pattern.compile("^http:\\/\\/ncode\\.syosetu\\.com\\/(\\w+)\\/$");
        Matcher m = p.matcher(url.toString());
        if (!m.matches()) {
            throw new RuntimeException("url not match novel url. url=" + url.toString());
        }

        String ncode = m.group(1);

        return ncode;
    }

    private boolean saveNovelIndex(URL url, String title, Date searchDate) {
        String ncode = this.extractNCode(url);

        if (this.repo.findOne(ncode) != null) {
            return false;
        }

        NovelIndex index = new NovelIndex();
        index.setNcode(ncode);
        index.setTitle(title);
        index.setSearchDate(searchDate);

        this.repo.save(index);

        return true;
    }

}
