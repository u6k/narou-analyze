
package me.u6k.narou_analyze.narou_crawler.controller;

import me.u6k.narou_analyze.narou_crawler.service.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrawlerController {

    private static final Logger L = LoggerFactory.getLogger(CrawlerController.class);

    @Autowired
    private CrawlerService service;

    @RequestMapping(value = "/api/novels/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IndexingNovelResult indexingNovel(@RequestBody IndexingNovelParam param) {
        L.debug("#indexingNovel: param={}", param);

        long count = this.service.indexingNovel(param.getSearchDate());

        IndexingNovelResult result = new IndexingNovelResult();
        result.setCount(count);

        return result;
    }

    @RequestMapping(value = "/api/novels/{ncode}/meta", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void getNovelMeta(@PathVariable("ncode") String ncode) {
        L.debug("#getNovelMeta: ncode={}", ncode);

        this.service.getNovelMeta(ncode);
    }

}
