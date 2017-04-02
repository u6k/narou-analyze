
package me.u6k.narou_analyze.narou_crawler;

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

    @Test
    public void test() {
        this.service.indexingNovel("http://yomou.syosetu.com/search.php?notnizi=1&word=&notword=&genre=&order=new&type=");
    }

}
