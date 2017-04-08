
package me.u6k.narou_analyze.narou_crawler.controller;

import java.net.URL;

import me.u6k.narou_analyze.narou_crawler.service.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CrawlerController {

    private static final Logger L = LoggerFactory.getLogger(CrawlerController.class);

    @Autowired
    private CrawlerService service;

    public void indexingNovel(URL searchPageUrl, int limit) {
        L.debug("#indexingNovel: searchPageUrl={}, limit={}", searchPageUrl, limit);

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
