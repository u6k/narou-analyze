
package me.u6k.narou_analyze.narou_crawler.controller;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.u6k.narou_analyze.narou_crawler.service.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerController {

    private static final Logger L = LoggerFactory.getLogger(CrawlerController.class);

    @Autowired
    private CrawlerService service;

    @Autowired
    private JmsMessagingTemplate jms;

    @RequestMapping(value = "/api/indexingNovel", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void indexingNovel(@RequestBody IndexingNovelParam param) {
        L.debug("#indexingNovel: param={}", param);

        String json;
        try {
            json = new ObjectMapper()
                            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                            .writeValueAsString(param);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Message<String> message = MessageBuilder.withPayload(json).build();
        this.jms.send("narou-crawler_indexingNovel", message);
    }

    @JmsListener(destination = "narou-crawler_indexingNovel")
    private void indexingNovel(Message<String> message) {
        L.debug("#indexingNovel from JMS: message={}", message);
        L.debug("payload={}", message.getPayload());

        IndexingNovelParam param;
        try {
            param = new ObjectMapper().readValue(message.getPayload(), IndexingNovelParam.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        URL searchPageUrl = param.getSearchPageUrl();
        int limit = param.getLimit();

        for (int i = 0; i < limit; i++) {
            L.debug("[{}/{}]indexing", i + 1, limit);
            searchPageUrl = this.service.indexingNovel(searchPageUrl);

            if (searchPageUrl == null) {
                L.debug("nextPage not found.");
                break;
            }
        }
    }

}
