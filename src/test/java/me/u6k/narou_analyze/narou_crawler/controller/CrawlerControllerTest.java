
package me.u6k.narou_analyze.narou_crawler.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerControllerTest {

    @Autowired
    private CrawlerController controller;

    @Autowired
    private NovelIndexRepository repo;

    @Before
    public void setup() {
        this.repo.deleteAllInBatch();
    }

    @Test
    public void indexingNovel() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=");
        this.controller.indexingNovel(searchPageUrl, 3);

        assertThat(this.repo.count(), is(60L));
    }

}
