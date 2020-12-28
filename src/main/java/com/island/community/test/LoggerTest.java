package com.island.community.test;

import com.island.community.CommunityApplication;
import com.island.community.dao.DiscussPostMapper;
import com.island.community.entity.DiscussPost;
import com.island.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
@MapperScan("com.island.community.dao")
public class LoggerTest {
    private static final Logger logger=LoggerFactory.getLogger(LoggerTest.class);

    @Test
    public void testLogger()
    {
        logger.debug("debug log");
        logger.info("info log");
        logger.warn("warm log");
        logger.error("error log");

    }
}
