package org.kbs.archiver;/**
 * User: kcn
 * Date: 12-9-26
 * Time: 下午5:12
 */

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.PersistenceExceptionTranslator;

public class MockSqlSessionTemplate extends SqlSessionTemplate {
    private static final Logger LOG = LoggerFactory.getLogger(MockSqlSessionTemplate.class);

    public MockSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        super(sqlSessionFactory);
    }

    @Override
    public int insert(String statement, Object parameter) {
        System.out.println("insert:" + statement + " data:" + parameter.toString());
        return 0;
    }
}
