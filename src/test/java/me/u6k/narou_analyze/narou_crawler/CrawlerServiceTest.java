
package me.u6k.narou_analyze.narou_crawler;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import me.u6k.narou_analyze.narou_crawler.model.NovelIndexRepository;
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
    private NovelIndexRepository repo;

    @Before
    public void setup() {
        this.repo.deleteAllInBatch();
    }

    @Test
    public void test() {
        this.service.indexingNovel("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=");

        assertThat(this.repo.count(), is(20L));
    }

}
