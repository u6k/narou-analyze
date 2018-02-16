
package me.u6k.narou_analyze.narou_crawler.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaData;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaDataRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelType;
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
    private NovelIndexRepository indexRepo;

    @Autowired
    private NovelMetaRepository metaRepo;

    @Autowired
    private NovelMetaDataRepository metaDataRepo;

    public long updateNovelIndex(Date searchDate) {
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

    public void downloadNovelMeta(String ncode) throws IOException {
        URL searchApiUrl = new URL("http://api.syosetu.com/novelapi/api/?gzip=5&out=json&ncode=" + ncode);
        String json = NetworkUtil.get(searchApiUrl, true);
        L.info("json={}", json);

        List<Map<String, Object>> jsonObj = new ObjectMapper().readValue(json, new TypeReference<List<Map<String, Object>>>() {
        });
        if (((int) jsonObj.get(0).get("allcount")) == 0) {
            throw new RuntimeException("allcount is 0.");
        }

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);
    }

    public void analyzeNovelMeta(String ncode) throws IOException, ParseException {
        NovelMetaData metaData = this.metaDataRepo.findOne(ncode);

        if (metaData == null) {
            throw new RuntimeException("ncode=" + ncode + " does not download.");
        }

        List<Map<String, Object>> jsonObj = new ObjectMapper().readValue(metaData.getData(), new TypeReference<List<Map<String, Object>>>() {
        });
        Map<String, Object> metaJsonObj = jsonObj.get(1);

        NovelMeta meta = new NovelMeta();
        meta.setNcode(this.buildString(metaJsonObj.get("ncode")).toLowerCase());
        meta.setTitle(this.buildString(metaJsonObj.get("title")));
        meta.setUserId(this.buildInt(metaJsonObj.get("userid")));
        meta.setWriter(this.buildString(metaJsonObj.get("writer")));
        meta.setStory(this.buildString(metaJsonObj.get("story")));
        meta.setBigGenre(this.buildInt(metaJsonObj.get("biggenre")));
        meta.setGenre(this.buildInt(metaJsonObj.get("genre")));
        meta.setKeywords(new ArrayList<>());
        for (String keyword : this.buildString(metaJsonObj.get("keyword")).split(" ")) {
            if (keyword.trim().length() > 0) {
                meta.getKeywords().add(keyword);
            }
        }
        meta.setGeneralFirstup(this.buildTimestamp(metaJsonObj.get("general_firstup")));
        meta.setGeneralLastup(this.buildTimestamp(metaJsonObj.get("general_lastup")));
        switch (this.buildInt(metaJsonObj.get("novel_type"))) {
            case 1:
                meta.setNovelType(NovelType.SERIES);
                break;
            case 2:
                meta.setNovelType(NovelType.SHORT);
                break;
            default:
                throw new RuntimeException("novel_type=" + metaJsonObj.get("novel_type") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("end"))) {
            case 0:
                meta.setEnd(true);
                break;
            case 1:
                meta.setEnd(false);
                break;
            default:
                throw new RuntimeException("end=" + metaJsonObj.get("end") + " is unknown.");
        }
        meta.setGeneralAllNo(this.buildInt(metaJsonObj.get("general_all_no")));
        meta.setLength(this.buildInt(metaJsonObj.get("length")));
        meta.setTime(this.buildInt(metaJsonObj.get("time")));
        switch (this.buildInt(metaJsonObj.get("isstop"))) {
            case 0:
                meta.setStop(true);
                break;
            case 1:
                meta.setStop(false);
                break;
            default:
                throw new RuntimeException("isstop=" + metaJsonObj.get("isstop") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("isr15"))) {
            case 0:
                meta.setR15(false);
                break;
            case 1:
                meta.setR15(true);
                break;
            default:
                throw new RuntimeException("isr15=" + metaJsonObj.get("isr15") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("isbl"))) {
            case 0:
                meta.setBoysLove(false);
                break;
            case 1:
                meta.setBoysLove(true);
                break;
            default:
                throw new RuntimeException("isbl=" + metaJsonObj.get("isbl") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("isgl"))) {
            case 0:
                meta.setGirlsLove(false);
                break;
            case 1:
                meta.setGirlsLove(true);
                break;
            default:
                throw new RuntimeException("isgl=" + metaJsonObj.get("isgl") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("iszankoku"))) {
            case 0:
                meta.setZankoku(false);
                break;
            case 1:
                meta.setZankoku(true);
                break;
            default:
                throw new RuntimeException("iszankoku=" + metaJsonObj.get("iszankoku") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("istensei"))) {
            case 0:
                meta.setTensei(false);
                break;
            case 1:
                meta.setTensei(true);
                break;
            default:
                throw new RuntimeException("istensei=" + metaJsonObj.get("istensei") + " is unknown.");
        }
        switch (this.buildInt(metaJsonObj.get("istenni"))) {
            case 0:
                meta.setTenni(false);
                break;
            case 1:
                meta.setTenni(true);
                break;
            default:
                throw new RuntimeException("istenni=" + metaJsonObj.get("istenni") + " is unknown.");
        }
        meta.setGlobalPoint(this.buildInt(metaJsonObj.get("global_point")));
        meta.setFavoriteNovelCount(this.buildInt(metaJsonObj.get("fav_novel_cnt")));
        meta.setReviewCount(this.buildInt(metaJsonObj.get("review_cnt")));
        meta.setAllPoint(this.buildInt(metaJsonObj.get("all_point")));
        meta.setAllHyokaCount(this.buildInt(metaJsonObj.get("all_hyoka_cnt")));
        meta.setSasieCount(this.buildInt(metaJsonObj.get("sasie_cnt")));
        meta.setKaiwaRitu(this.buildInt(metaJsonObj.get("kaiwaritu")));
        meta.setNovelUpdatedAt(this.buildTimestamp(metaJsonObj.get("novelupdated_at")));
        meta.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaRepo.save(meta);
    }

    private URL buildSearchPageUrl(Date searchDate) throws MalformedURLException {
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

    private String extractNCode(URL url) {
        Pattern p = Pattern.compile("^http[s]{0,1}:\\/\\/ncode\\.syosetu\\.com\\/(\\w+)\\/$");
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

    private Timestamp buildTimestamp(Object obj) throws ParseException {
        String dateTime = String.valueOf(obj);
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        Timestamp timestamp = new Timestamp(date.getTime());

        return timestamp;
    }

    private String buildString(Object obj) {
        String str = String.valueOf(obj);

        return str;
    }

    private int buildInt(Object obj) {
        int value = Integer.valueOf(String.valueOf(obj));

        return value;
    }

}
