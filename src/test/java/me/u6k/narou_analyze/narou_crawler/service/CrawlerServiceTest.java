
package me.u6k.narou_analyze.narou_crawler.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMeta;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerServiceTest {

    @Autowired
    private CrawlerService service;

    @Autowired
    private NovelIndexRepository indexRepo;

    @Autowired
    private NovelMetaRepository metaRepo;

    @Before
    public void setup() {
        this.indexRepo.deleteAllInBatch();
        this.metaRepo.deleteAllInBatch();
    }

    @Test
    public void extractNCode() throws Exception {
        URL url = new URL("http://ncode.syosetu.com/n4830bu/");
        String ncode = this.service.extractNCode(url);

        assertThat(ncode, is("n4830bu"));
    }

    @Test
    public void buildSearchPageUrl() throws Exception {
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
        URL searchPageUrl = this.service.buildSearchPageUrl(searchDate);

        URL expectedUrl = new URL("http://yomou.syosetu.com/search.php?mintime=&maxtime=&minlen=&maxlen=&minlastup=2010%2F01%2F01&maxlastup=2010%2F01%2F01&order=old&type=&genre=&word=&notword=");

        assertThat(searchPageUrl, is(expectedUrl));
    }

    @Test
    public void indexingNovel_0件でも正常動作() throws Exception {
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2004-04-01");
        long count = this.service.indexingNovel(searchDate);

        assertThat(count, is(0L));
        assertThat(this.indexRepo.count(), is(count));
    }

    @Test
    public void indexingNovel_複数ページでも正常動作() throws Exception {
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
        long count = this.service.indexingNovel(searchDate);

        assertThat(count, greaterThan(21L));
        assertThat(this.indexRepo.count(), is(count));
    }

    @Test
    public void indexingNovel_複数回実行しても正常動作() throws Exception {
        for (int i = 0; i < 3; i++) {
            Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
            this.service.indexingNovel(searchDate);
        }
    }

    @Test
    public void getNovelMeta_本好きの下剋上を取得() {
        String ncode = "n4830bu";

        this.service.getNovelMeta(ncode);

        NovelMeta meta = this.metaRepo.findOne(ncode);

        assertNotNull(meta);
        assertThat(meta.getNcode(), is("n4830bu"));
        assertNotNull(meta.getUpdated());
    }

    @Test
    public void getNovelMeta_複数回実行しても正常動作() {
        String ncode = "n4830bu";

        this.service.getNovelMeta(ncode);
        this.service.getNovelMeta(ncode);
    }

    @Test
    public void getNovelMeta_存在しないNコードの場合はエラー() {
        String ncode = "n9999zz";

        try {
            this.service.getNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

}
