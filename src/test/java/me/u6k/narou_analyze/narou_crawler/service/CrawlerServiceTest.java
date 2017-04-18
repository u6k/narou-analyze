
package me.u6k.narou_analyze.narou_crawler.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMeta;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaData;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaDataRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelType;
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

    @Autowired
    private NovelMetaRepository metaRepo;

    @Before
    public void setup() {
        this.indexRepo.deleteAllInBatch();
        this.metaRepo.deleteAllInBatch();
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

    @Test
    public void analyzeNovelMeta_正しいJSONを解析できる() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        this.service.analyzeNovelMeta(ncode);

        NovelMeta meta = this.metaRepo.findOne(ncode);
        assertThat(meta.getNcode(), is("N4830BU"));
        assertThat(meta.getTitle(), is("本好きの下剋上　～司書になるためには手段を選んでいられません～"));
        assertThat(meta.getUserId(), is(372556));
        assertThat(meta.getWriter(), is("香月　美夜"));
        assertThat(meta.getStory(), is("　本が好きで、司書資格を取り、大学図書館への就職が決まっていたのに、大学卒業直後に死んでしまった麗乃。転生したのは、識字率が低くて本が少ない世界の兵士の娘。いくら読みたくても周りに本なんてあるはずない。本がないならどうする？　作ってしまえばいいじゃない。目指すは図書館司書！　本に囲まれて生きるため、本を作るところから始めよう。※最初の主人公の性格が最悪です。ある程度成長するまで、気分悪くなる恐れがあります。（R15は念のため）"));
        assertThat(meta.getBigGenre(), is(2));
        assertThat(meta.getGenre(), is(201));
        assertThat(meta.getKeywords().size(), is(5));
        assertThat(meta.getKeywords(), contains("R15", "異世界転生", "ファンタジー", "異世界", "転生", "女主人公"));
        assertThat(meta.getGeneralFirstup(), is(buildTimestamp("2013-09-23 13:35:08")));
        assertThat(meta.getGeneralLastup(), is(buildTimestamp("2017-03-12 12:18:40")));
        assertThat(meta.getNovelType(), is(NovelType.SERIES));
        assertTrue(meta.isEnd());
        assertThat(meta.getGeneralAllNo(), is(677));
        assertThat(meta.getLength(), is(5682766));
        assertThat(meta.getTime(), is(11366));
        assertFalse(meta.isStop());
        assertTrue(meta.isR15());
        assertFalse(meta.isBoysLove());
        assertFalse(meta.isGirlsLove());
        assertFalse(meta.isZankoku());
        assertTrue(meta.isTensei());
        assertFalse(meta.isTenni());
        assertThat(meta.getGlobalPoint(), is(168305));
        assertThat(meta.getFavoriteNovelCount(), is(47285));
        assertThat(meta.getReviewCount(), is(60));
        assertThat(meta.getAllPoint(), is(73735));
        assertThat(meta.getAllHyokaCount(), is(7522));
        assertThat(meta.getSasieCount(), is(0));
        assertThat(meta.getKaiwaRitu(), is(50));
        assertThat(meta.getNovelUpdatedAt(), is(buildTimestamp("2017-03-12 12:21:33")));
        assertNotNull(meta.getUpdated());
    }

    @Test
    public void analyzeNovelMeta_正しいJSONを解析できる_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\",\"ncode\":\"N4830BU\",\"userid\":0,\"writer\":\"\",\"story\":\"\",\"biggenre\":0,\"genre\":0,\"gensaku\":\"\",\"keyword\":\"\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":2,\"end\":1,\"general_all_no\":0,\"length\":0,\"time\":0,\"isstop\":1,\"isr15\":0,\"isbl\":1,\"isgl\":1,\"iszankoku\":1,\"istensei\":0,\"istenni\":1,\"pc_or_k\":0,\"global_point\":0,\"fav_novel_cnt\":0,\"review_cnt\":0,\"all_point\":0,\"all_hyoka_cnt\":0,\"sasie_cnt\":123,\"kaiwaritu\":12,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        this.service.analyzeNovelMeta(ncode);

        NovelMeta meta = this.metaRepo.findOne(ncode);
        assertThat(meta.getNcode(), is("N4830BU"));
        assertThat(meta.getTitle(), is(""));
        assertThat(meta.getUserId(), is(0));
        assertThat(meta.getWriter(), is(""));
        assertThat(meta.getStory(), is(""));
        assertThat(meta.getBigGenre(), is(0));
        assertThat(meta.getGenre(), is(0));
        assertThat(meta.getKeywords().size(), is(0));
        assertThat(meta.getGeneralFirstup(), is(buildTimestamp("2013-09-23 13:35:08")));
        assertThat(meta.getGeneralLastup(), is(buildTimestamp("2017-03-12 12:18:40")));
        assertThat(meta.getNovelType(), is(NovelType.SHORT));
        assertFalse(meta.isEnd());
        assertThat(meta.getGeneralAllNo(), is(0));
        assertThat(meta.getLength(), is(0));
        assertThat(meta.getTime(), is(0));
        assertTrue(meta.isStop());
        assertFalse(meta.isR15());
        assertTrue(meta.isBoysLove());
        assertTrue(meta.isGirlsLove());
        assertTrue(meta.isZankoku());
        assertFalse(meta.isTensei());
        assertTrue(meta.isTenni());
        assertThat(meta.getGlobalPoint(), is(0));
        assertThat(meta.getFavoriteNovelCount(), is(0));
        assertThat(meta.getReviewCount(), is(0));
        assertThat(meta.getAllPoint(), is(0));
        assertThat(meta.getAllHyokaCount(), is(0));
        assertThat(meta.getSasieCount(), is(12));
        assertThat(meta.getKaiwaRitu(), is(0));
        assertThat(meta.getNovelUpdatedAt(), is(buildTimestamp("2017-03-12 12:21:33")));
        assertNotNull(meta.getUpdated());
    }

    @Test
    public void analyzeNovelMeta_keywordsの変更を反映できる() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        this.service.analyzeNovelMeta(ncode);

        NovelMeta meta = this.metaRepo.findOne(ncode);
        assertThat(meta.getNcode(), is("N4830BU"));
        assertThat(meta.getTitle(), is("本好きの下剋上　～司書になるためには手段を選んでいられません～"));
        assertThat(meta.getUserId(), is(372556));
        assertThat(meta.getWriter(), is("香月　美夜"));
        assertThat(meta.getStory(), is("　本が好きで、司書資格を取り、大学図書館への就職が決まっていたのに、大学卒業直後に死んでしまった麗乃。転生したのは、識字率が低くて本が少ない世界の兵士の娘。いくら読みたくても周りに本なんてあるはずない。本がないならどうする？　作ってしまえばいいじゃない。目指すは図書館司書！　本に囲まれて生きるため、本を作るところから始めよう。※最初の主人公の性格が最悪です。ある程度成長するまで、気分悪くなる恐れがあります。（R15は念のため）"));
        assertThat(meta.getBigGenre(), is(2));
        assertThat(meta.getGenre(), is(201));
        assertThat(meta.getKeywords().size(), is(5));
        assertThat(meta.getKeywords(), contains("R15", "異世界転生", "ファンタジー", "異世界", "転生", "女主人公"));
        assertThat(meta.getGeneralFirstup(), is(buildTimestamp("2013-09-23 13:35:08")));
        assertThat(meta.getGeneralLastup(), is(buildTimestamp("2017-03-12 12:18:40")));
        assertThat(meta.getNovelType(), is(NovelType.SERIES));
        assertTrue(meta.isEnd());
        assertThat(meta.getGeneralAllNo(), is(677));
        assertThat(meta.getLength(), is(5682766));
        assertThat(meta.getTime(), is(11366));
        assertFalse(meta.isStop());
        assertTrue(meta.isR15());
        assertFalse(meta.isBoysLove());
        assertFalse(meta.isGirlsLove());
        assertFalse(meta.isZankoku());
        assertTrue(meta.isTensei());
        assertFalse(meta.isTenni());
        assertThat(meta.getGlobalPoint(), is(168305));
        assertThat(meta.getFavoriteNovelCount(), is(47285));
        assertThat(meta.getReviewCount(), is(60));
        assertThat(meta.getAllPoint(), is(73735));
        assertThat(meta.getAllHyokaCount(), is(7522));
        assertThat(meta.getSasieCount(), is(0));
        assertThat(meta.getKaiwaRitu(), is(50));
        assertThat(meta.getNovelUpdatedAt(), is(buildTimestamp("2017-03-12 12:21:33")));
        assertNotNull(meta.getUpdated());

        String json2 = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"aaa bbb ccc\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        metaData = this.metaDataRepo.findOne(ncode);
        metaData.setData(json2.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        this.service.analyzeNovelMeta(ncode);

        meta = this.metaRepo.findOne(ncode);
        assertThat(meta.getNcode(), is("N4830BU"));
        assertThat(meta.getTitle(), is("本好きの下剋上　～司書になるためには手段を選んでいられません～"));
        assertThat(meta.getUserId(), is(372556));
        assertThat(meta.getWriter(), is("香月　美夜"));
        assertThat(meta.getStory(), is("　本が好きで、司書資格を取り、大学図書館への就職が決まっていたのに、大学卒業直後に死んでしまった麗乃。転生したのは、識字率が低くて本が少ない世界の兵士の娘。いくら読みたくても周りに本なんてあるはずない。本がないならどうする？　作ってしまえばいいじゃない。目指すは図書館司書！　本に囲まれて生きるため、本を作るところから始めよう。※最初の主人公の性格が最悪です。ある程度成長するまで、気分悪くなる恐れがあります。（R15は念のため）"));
        assertThat(meta.getBigGenre(), is(2));
        assertThat(meta.getGenre(), is(201));
        assertThat(meta.getKeywords().size(), is(3));
        assertThat(meta.getKeywords(), contains("aaa", "bbb", "ccc"));
        assertThat(meta.getGeneralFirstup(), is(buildTimestamp("2013-09-23 13:35:08")));
        assertThat(meta.getGeneralLastup(), is(buildTimestamp("2017-03-12 12:18:40")));
        assertThat(meta.getNovelType(), is(NovelType.SERIES));
        assertTrue(meta.isEnd());
        assertThat(meta.getGeneralAllNo(), is(677));
        assertThat(meta.getLength(), is(5682766));
        assertThat(meta.getTime(), is(11366));
        assertFalse(meta.isStop());
        assertTrue(meta.isR15());
        assertFalse(meta.isBoysLove());
        assertFalse(meta.isGirlsLove());
        assertFalse(meta.isZankoku());
        assertTrue(meta.isTensei());
        assertFalse(meta.isTenni());
        assertThat(meta.getGlobalPoint(), is(168305));
        assertThat(meta.getFavoriteNovelCount(), is(47285));
        assertThat(meta.getReviewCount(), is(60));
        assertThat(meta.getAllPoint(), is(73735));
        assertThat(meta.getAllHyokaCount(), is(7522));
        assertThat(meta.getSasieCount(), is(0));
        assertThat(meta.getKaiwaRitu(), is(50));
        assertThat(meta.getNovelUpdatedAt(), is(buildTimestamp("2017-03-12 12:21:33")));
        assertNotNull(meta.getUpdated());
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_novelType_1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":0,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("novel_type=0 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_novelType_3() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":3,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("novel_type=3 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_end_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":-1,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("end=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_end_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":2,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("end=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isStop_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":-1,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isstop=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isStop_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":2,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isstop=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isR15_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":-1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isr15=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isR15_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":2,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isr15=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isBL_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":-1,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isbl=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isBL_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":2,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isbl=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isGL_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":-1,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isgl=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isGL_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":2,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("isgl=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isZankoku_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":-1,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("iszankoku=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isZankoku_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":2,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("iszankoku=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isTensei_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":-1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("istensei=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isTensei_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":2,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("istensei=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isTenni_minus1() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":0,\"istenni\":-1,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("istenni=-1 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未知のJSON値はエラー_isTenni_2() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":0,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":0,\"istenni\":2,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta(ncode);

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("istenni=2 is unknown."));
        }

        assertThat(this.metaRepo.count(), is(0L));
    }

    @Test
    public void analyzeNovelMeta_未ダウンロードの場合はエラー() throws Exception {
        String ncode = "n4830bu";
        String json = "[{\"allcount\":1},{\"title\":\"\u672c\u597d\u304d\u306e\u4e0b\u524b\u4e0a\u3000\uff5e\u53f8\u66f8\u306b\u306a\u308b\u305f\u3081\u306b\u306f\u624b\u6bb5\u3092\u9078\u3093\u3067\u3044\u3089\u308c\u307e\u305b\u3093\uff5e\",\"ncode\":\"N4830BU\",\"userid\":372556,\"writer\":\"\u9999\u6708\u3000\u7f8e\u591c\",\"story\":\"\u3000\u672c\u304c\u597d\u304d\u3067\u3001\u53f8\u66f8\u8cc7\u683c\u3092\u53d6\u308a\u3001\u5927\u5b66\u56f3\u66f8\u9928\u3078\u306e\u5c31\u8077\u304c\u6c7a\u307e\u3063\u3066\u3044\u305f\u306e\u306b\u3001\u5927\u5b66\u5352\u696d\u76f4\u5f8c\u306b\u6b7b\u3093\u3067\u3057\u307e\u3063\u305f\u9e97\u4e43\u3002\u8ee2\u751f\u3057\u305f\u306e\u306f\u3001\u8b58\u5b57\u7387\u304c\u4f4e\u304f\u3066\u672c\u304c\u5c11\u306a\u3044\u4e16\u754c\u306e\u5175\u58eb\u306e\u5a18\u3002\u3044\u304f\u3089\u8aad\u307f\u305f\u304f\u3066\u3082\u5468\u308a\u306b\u672c\u306a\u3093\u3066\u3042\u308b\u306f\u305a\u306a\u3044\u3002\u672c\u304c\u306a\u3044\u306a\u3089\u3069\u3046\u3059\u308b\uff1f\u3000\u4f5c\u3063\u3066\u3057\u307e\u3048\u3070\u3044\u3044\u3058\u3083\u306a\u3044\u3002\u76ee\u6307\u3059\u306f\u56f3\u66f8\u9928\u53f8\u66f8\uff01\u3000\u672c\u306b\u56f2\u307e\u308c\u3066\u751f\u304d\u308b\u305f\u3081\u3001\u672c\u3092\u4f5c\u308b\u3068\u3053\u308d\u304b\u3089\u59cb\u3081\u3088\u3046\u3002\u203b\u6700\u521d\u306e\u4e3b\u4eba\u516c\u306e\u6027\u683c\u304c\u6700\u60aa\u3067\u3059\u3002\u3042\u308b\u7a0b\u5ea6\u6210\u9577\u3059\u308b\u307e\u3067\u3001\u6c17\u5206\u60aa\u304f\u306a\u308b\u6050\u308c\u304c\u3042\u308a\u307e\u3059\u3002\uff08R15\u306f\u5ff5\u306e\u305f\u3081\uff09\",\"biggenre\":2,\"genre\":201,\"gensaku\":\"\",\"keyword\":\"R15 \u7570\u4e16\u754c\u8ee2\u751f \u30d5\u30a1\u30f3\u30bf\u30b8\u30fc \u7570\u4e16\u754c \u8ee2\u751f \u5973\u4e3b\u4eba\u516c\",\"general_firstup\":\"2013-09-23 13:35:08\",\"general_lastup\":\"2017-03-12 12:18:40\",\"novel_type\":1,\"end\":0,\"general_all_no\":677,\"length\":5682766,\"time\":11366,\"isstop\":0,\"isr15\":1,\"isbl\":0,\"isgl\":0,\"iszankoku\":0,\"istensei\":1,\"istenni\":0,\"pc_or_k\":2,\"global_point\":168305,\"fav_novel_cnt\":47285,\"review_cnt\":60,\"all_point\":73735,\"all_hyoka_cnt\":7522,\"sasie_cnt\":0,\"kaiwaritu\":50,\"novelupdated_at\":\"2017-03-12 12:21:33\",\"updated_at\":\"2017-04-18 18:19:36\"}]";

        NovelMetaData metaData = new NovelMetaData();
        metaData.setNcode(ncode);
        metaData.setData(json.getBytes("UTF-8"));
        metaData.setUpdated(new Timestamp(System.currentTimeMillis()));

        this.metaDataRepo.save(metaData);

        try {
            this.service.analyzeNovelMeta("n9999zz");

            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("ncode=n9999zz does not download."));
        }
    }

    private Timestamp buildTimestamp(String dateTime) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        Timestamp timestamp = new Timestamp(date.getTime());

        return timestamp;
    }

}
