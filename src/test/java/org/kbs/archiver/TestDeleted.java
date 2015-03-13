package org.kbs.archiver;/**
 * User: kcn
 * Date: 12-9-26
 * Time: 下午5:15
 */

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kbs.library.InitTest;
import org.kbs.library.SimpleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-test.xml"})
public class TestDeleted {
    private static final Logger LOG = LoggerFactory.getLogger(TestDeleted.class);

//    @Ignore("damage data")
    @Test
    public void test() {
        BoardEntity theboard=new BoardEntity();
        String boardname="Progress";
        theboard.setName(boardname);
        theboard.setCname("测试用");
        theboard.setLastarticleid(0);
        theboard.setBoardid(100011);
        theboard.setThreads(0);
        theboard.setArticles(0);
        theboard.setIshidden(false);
//        theboard.setLastdeletedid(-1);
        try {
            ArchiverBoardImpl worker=new ArchiverBoardImpl(InitTest.getAppContext(),null, "src/test/resources/data");
            worker.work(theboard);
        } catch (SimpleException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
