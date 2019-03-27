
package me.u6k.narou_analyze.narou_crawler.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMeta;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaData;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaDataRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelType;
import me.u6k.narou_analyze.narou_crawler.service.CrawlerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CrawlerControllerTest {

    private static final Logger L = LoggerFactory.getLogger(CrawlerControllerTest.class);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private NovelIndexRepository indexRepo;

    @Autowired
    private NovelMetaRepository metaRepo;

    @Autowired
    private NovelMetaDataRepository metaDataRepo;

    @Autowired
    private CrawlerService service;

    @Before
    public void setup() {
        this.indexRepo.deleteAll();
        this.metaRepo.deleteAll();
        this.metaDataRepo.deleteAll();

        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private ResultActions perform(MockMvc mvc, RequestBuilder request) throws Exception {
        ResultActions result = mvc.perform(request);

        MockHttpServletResponse response = result.andReturn().getResponse();
        L.debug("response: status={}, body={}", response.getStatus(), response.getContentAsString());

        return result;
    }

    @Test
    public void updateNovelIndex() throws Exception {
        String json = "{\"searchDate\":\"2010-01-01\"}";
        L.debug("request: json={}", json);

        ResultActions result = perform(mvc, post("/api/novels/")
                        .contentType("application/json")
                        .content(json));

        long count = this.indexRepo.count();
        assertThat(count, greaterThan(21L));

        result.andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$.count", is((int) count)));
    }

    @Test
    public void downloadNovelMeta() throws Exception {
        ResultActions result = perform(mvc, post("/api/novels/n4830bu/meta/download"));

        result.andExpect(status().isNoContent());

        NovelMetaData metaData = this.metaDataRepo.findOne("n4830bu");
        assertThat(metaData.getNcode(), is("n4830bu"));
        assertThat(metaData.getData().length, greaterThan(0));
        assertNotNull(metaData.getUpdated());
    }

    @Test
    public void analyzeNovelMeta() throws Exception {
        ResultActions result = perform(mvc, post("/api/novels/n4830bu/meta/download"));

        result.andExpect(status().isNoContent());

        result = perform(mvc, post("/api/novels/n4830bu/meta/"));

        result.andExpect(status().isNoContent());

        NovelMeta meta = this.metaRepo.findOne("n4830bu");
        assertThat(meta.getNcode(), is("n4830bu"));
        assertThat(meta.getTitle(), is("本好きの下剋上　～司書になるためには手段を選んでいられません～"));
        assertThat(meta.getUserId(), is(372556));
        assertThat(meta.getWriter(), is("香月　美夜"));
        assertThat(meta.getStory(), is("　本が好きで、司書資格を取り、大学図書館への就職が決まっていたのに、大学卒業直後に死んでしまった麗乃。転生したのは、識字率が低くて本が少ない世界の兵士の娘。いくら読みたくても周りに本なんてあるはずない。本がないならどうする？　作ってしまえばいいじゃない。目指すは図書館司書！　本に囲まれて生きるため、本を作るところから始めよう。※最初の主人公の性格が最悪です。ある程度成長するまで、気分悪くなる恐れがあります。（R15は念のため）"));
        assertThat(meta.getBigGenre(), is(2));
        assertThat(meta.getGenre(), is(201));
        assertThat(meta.getKeywords().size(), is(6));
        assertThat(meta.getKeywords(), contains("R15", "異世界転生", "ファンタジー", "異世界", "転生", "女主人公"));
        assertThat(meta.getGeneralFirstup(), is(buildTimestamp("2013-09-23 13:35:08")));
        assertThat(meta.getGeneralLastup(), is(buildTimestamp("2017-03-12 12:18:40")));
        assertThat(meta.getNovelType(), is(NovelType.SERIES));
        assertTrue(meta.isEnd());
        assertThat(meta.getGeneralAllNo(), is(677));
        assertThat(meta.getLength(), is(5682766));
        assertThat(meta.getTime(), is(11366));
        assertTrue(meta.isStop());
        assertTrue(meta.isR15());
        assertFalse(meta.isBoysLove());
        assertFalse(meta.isGirlsLove());
        assertFalse(meta.isZankoku());
        assertTrue(meta.isTensei());
        assertFalse(meta.isTenni());
        assertThat(meta.getGlobalPoint(), greaterThan(0));
        assertThat(meta.getFavoriteNovelCount(), greaterThan(0));
        assertThat(meta.getReviewCount(), greaterThan(0));
        assertThat(meta.getAllPoint(), greaterThan(0));
        assertThat(meta.getAllHyokaCount(), greaterThan(0));
        assertThat(meta.getSasieCount(), is(0));
        assertThat(meta.getKaiwaRitu(), is(50));
        assertThat(meta.getNovelUpdatedAt(), is(buildTimestamp("2017-03-12 12:21:33")));
        assertNotNull(meta.getUpdated());
    }

    @Test
    public void findNovelIndex() throws Exception {
        Date searchDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-01");
        this.service.updateNovelIndex(searchDate1);

        Date searchDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-02");
        this.service.updateNovelIndex(searchDate2);

        Date searchDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("2010-04-03");
        this.service.updateNovelIndex(searchDate3);

        ResultActions result = perform(mvc, get("/api/ncodes/2010-04-02"));

        result.andExpect(status().isOk())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andExpect(jsonPath("$").isArray());
    }

    private Timestamp buildTimestamp(String dateTime) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        Timestamp timestamp = new Timestamp(date.getTime());

        return timestamp;
    }

}
