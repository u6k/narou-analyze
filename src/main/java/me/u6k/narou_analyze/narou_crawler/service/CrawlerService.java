
package me.u6k.narou_analyze.narou_crawler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.u6k.narou_analyze.narou_crawler.model.NovelIndex;
import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMeta;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaRepository;
import me.u6k.narou_analyze.narou_crawler.util.NetworkUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
    private NovelIndexRepository indexRepo;

    @Autowired
    private NovelMetaRepository metaRepo;

    public long indexingNovel(Date searchDate) {
        try {
            long count = 0;

            URL searchPageUrl = this.buildSearchPageUrl(searchDate);
            while (searchPageUrl != null) {
                String html = NetworkUtil.get(searchPageUrl);

                Document htmlDoc = Jsoup.parse(html);

                Elements novelLinks = htmlDoc.select("div.novel_h a.tl");
                for (int i = 0; i < novelLinks.size(); i++) {
                    Element novelLink = novelLinks.get(i);
                    URL novelUrl = new URL(novelLink.attr("href"));
                    String novelTitle = novelLink.text();
                    L.debug("novel: url={}, title={}", novelUrl, novelTitle);

                    boolean saveResult = this.saveNovelIndex(novelUrl, novelTitle, searchDate);
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

    public List<String> findNovelIndex(Date searchDate) {
        if (searchDate == null) {
            throw new IllegalArgumentException("searchDate is null.");
        }

        Calendar c = Calendar.getInstance();
        c.setTime(searchDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        searchDate = c.getTime();

        List<NovelIndex> indexes = this.indexRepo.findBySearchDate(searchDate);
        List<String> ncodes = indexes.stream()
                        .map(x -> x.getNcode())
                        .collect(Collectors.toList());

        return ncodes;
    }

    public void getNovelMeta(String ncode) {
        try {
            URL searchApiUrl = new URL("http://api.syosetu.com/novelapi/api/?gzip=5&out=json&ncode=" + ncode);
            String json = NetworkUtil.get(searchApiUrl, true);

            List<Map<String, Object>> jsonObj = new ObjectMapper().readValue(json, new TypeReference<List<Map<String, Object>>>() {
            });
            L.info("jsonObj={}", ToStringBuilder.reflectionToString(jsonObj));

            NovelMeta meta = new NovelMeta();
            meta.setNcode(ncode);
            meta.setTitle((String) jsonObj.get(1).get("title"));
            meta.setData(json.getBytes("UTF-8"));
            meta.setUpdated(new Timestamp(System.currentTimeMillis()));

            this.metaRepo.save(meta);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL buildSearchPageUrl(Date searchDate) throws MalformedURLException {
        SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormatter = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd");

        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?mintime=&maxtime=&minlen=&maxlen=&minlastup="
                        + yearFormatter.format(searchDate) + "%2F" + monthFormatter.format(searchDate) + "%2F" + dayFormatter.format(searchDate)
                        + "&maxlastup="
                        + yearFormatter.format(searchDate) + "%2F" + monthFormatter.format(searchDate) + "%2F" + dayFormatter.format(searchDate)
                        + "&order=old&type=&genre=&word=&notword=");

        return searchPageUrl;
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

        if (this.indexRepo.findOne(ncode) != null) {
            return false;
        }

        NovelIndex index = new NovelIndex();
        index.setNcode(ncode);
        index.setTitle(title);
        index.setSearchDate(searchDate);

        this.indexRepo.save(index);

        return true;
    }

}
