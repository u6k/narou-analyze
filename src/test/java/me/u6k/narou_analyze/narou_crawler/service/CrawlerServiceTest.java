
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
    public void indexingNovel_0件でも正常動作() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?mintime=&maxtime=&minlen=&maxlen=&minlastup=2004%2F04%2F01&maxlastup=2004%2F04%2F01&order=old&type=&genre=&word=&notword=");
        long count = this.service.indexingNovel(searchPageUrl);

        assertThat(count, is(0L));
        assertThat(this.repo.count(), is(count));
    }

    @Test
    public void indexingNovel_1ページでも正常動作() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?mintime=&maxtime=&minlen=&maxlen=&minlastup=2004%2F04%2F01&maxlastup=2004%2F05%2F01&order=old&type=&genre=&word=&notword=");
        long count = this.service.indexingNovel(searchPageUrl);

        assertThat(count, is(1L));
        assertThat(this.repo.count(), is(count));
    }

    @Test
    public void indexingNovel_複数ページでも正常動作() throws Exception {
        URL searchPageUrl = new URL("http://yomou.syosetu.com/search.php?mintime=&maxtime=&minlen=&maxlen=&minlastup=2014%2F04%2F01&maxlastup=2014%2F04%2F01&order=old&type=&genre=&word=&notword=");
        long count = this.service.indexingNovel(searchPageUrl);

        assertThat(count, greaterThan(200L));
        assertThat(this.repo.count(), is(count));
    }

}
