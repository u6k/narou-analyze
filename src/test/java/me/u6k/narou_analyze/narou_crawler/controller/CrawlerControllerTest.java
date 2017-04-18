
package me.u6k.narou_analyze.narou_crawler.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaData;
import me.u6k.narou_analyze.narou_crawler.model.NovelMetaDataRepository;
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
    private NovelMetaDataRepository metaDataRepo;

    @Autowired
    private CrawlerService service;

    @Before
    public void setup() {
        this.indexRepo.deleteAllInBatch();
        this.metaDataRepo.deleteAllInBatch();

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

}
