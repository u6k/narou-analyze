
package me.u6k.narou_analyze.narou_crawler.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
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
    private NovelIndexRepository repo;

    @Before
    public void setup() {
        this.repo.deleteAllInBatch();

        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private String buildJson(Object obj) throws JsonProcessingException {
        String json = new ObjectMapper()
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                        .writeValueAsString(obj);
        L.debug("request: json={}", json);

        return json;
    }

    private ResultActions perform(MockMvc mvc, RequestBuilder request) throws Exception {
        ResultActions result = mvc.perform(request);

        MockHttpServletResponse response = result.andReturn().getResponse();
        L.debug("response: status={}, body={}", response.getStatus(), response.getContentAsString());

        return result;
    }

    @Test
    public void indexingNovel() throws Exception {
        IndexingNovelParam param = new IndexingNovelParam();
        param.setSearchPageUrl(new URL("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type="));
        param.setLimit(3);
        String json = this.buildJson(param);

        ResultActions result = perform(mvc, post("/api/indexingNovel")
                        .contentType("application/json")
                        .content(json));

        result.andExpect(status().isNoContent());
        assertThat(this.repo.count(), is(0L));

        Thread.sleep(60000);

        assertThat(this.repo.count(), is(60L));
    }

}
