
package me.u6k.narou_analyze.narou_crawler.service;

import static org.hamcrest.Matchers.*;
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
    public void indexingNovel() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=");
        URL nextUrl = this.service.indexingNovel(searchPageUrl);

        assertThat(this.repo.count(), is(20L));
        assertThat(nextUrl, is(new URL("http://yomou.syosetu.com/search.php?&order=new&notnizi=1&p=2")));
    }

    @Test
    public void indexingNovel_複数回実行しても正常動作する() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=");
        this.service.indexingNovel(searchPageUrl);
        this.service.indexingNovel(searchPageUrl);

        assertThat(this.repo.count(), greaterThan(0L));
    }

}
