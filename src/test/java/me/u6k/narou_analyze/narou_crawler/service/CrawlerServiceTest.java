
package me.u6k.narou_analyze.narou_crawler.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaData;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaDataRepository;
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
    private NovelMetaDataRepository metaDataRepo;

    @Before
    public void setup() {
        this.indexRepo.deleteAllInBatch();
        this.metaDataRepo.deleteAllInBatch();
    }

    @Test
    public void updateNovelIndex_0件でも正常動作() throws Exception {
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2004-04-01");
        long count = this.service.updateNovelIndex(searchDate);

        assertThat(count, is(0L));
        assertThat(this.indexRepo.count(), is(count));
    }

    @Test
    public void updateNovelIndex_複数ページでも正常動作() throws Exception {
        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
        long count = this.service.updateNovelIndex(searchDate);

        assertThat(count, greaterThan(21L));
        assertThat(this.indexRepo.count(), is(count));
    }

    @Test
    public void updateNovelIndex_複数回実行しても正常動作() throws Exception {
        for (int i = 0; i < 3; i++) {
            Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
            this.service.updateNovelIndex(searchDate);
        }
    }

    @Test
    public void downloadNovelMeta_本好きの下剋上を取得() throws Exception {
        String ncode = "n4830bu";

        this.service.downloadNovelMeta(ncode);

        NovelMetaData metaData = this.metaDataRepo.findOne(ncode);

        assertNotNull(metaData);
        assertThat(metaData.getNcode(), is("n4830bu"));
        assertThat(metaData.getData().length, greaterThan(0));
        assertNotNull(metaData.getUpdated());
    }

    @Test
    public void downloadNovelMeta_複数回実行しても正常動作() throws Exception {
        String ncode = "n4830bu";

        this.service.downloadNovelMeta(ncode);
        this.service.downloadNovelMeta(ncode);
    }

    @Test
    public void downloadNovelMeta_存在しないNコードの場合はエラー() throws Exception {
        String ncode = "n9999zz";

        try {
            this.service.downloadNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("allcount is 0."));
        }
    }

    @Test
    public void findNovelIndex_存在する検索日付でNコードを取得() throws Exception {
        Date searchDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-01");
        long count1 = this.service.updateNovelIndex(searchDate1);

        Date searchDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-02");
        long count2 = this.service.updateNovelIndex(searchDate2);

        Date searchDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-03");
        long count3 = this.service.updateNovelIndex(searchDate3);

        List<String> ncodes1 = this.service.findNovelIndex(searchDate1);
        assertThat((long) ncodes1.size(), is(count1));

        List<String> ncodes2 = this.service.findNovelIndex(searchDate2);
        assertThat((long) ncodes2.size(), is(count2));

        List<String> ncodes3 = this.service.findNovelIndex(searchDate3);
        assertThat((long) ncodes3.size(), is(count3));
    }

    @Test
    public void findNovelIndex_存在しない検索日付の場合は0件() throws Exception {
        Date searchDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-01");
        this.service.updateNovelIndex(searchDate1);

        Date searchDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-02");
        this.service.updateNovelIndex(searchDate2);

        Date searchDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-03");
        this.service.updateNovelIndex(searchDate3);

        Date searchDate = new SimpleDateFormat("yyyy-MM-dd").parse("2004-01-01");
        List<String> ncodes1 = this.service.findNovelIndex(searchDate);

        assertThat(ncodes1.size(), is(0));
    }

}
