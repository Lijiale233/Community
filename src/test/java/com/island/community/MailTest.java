package com.island.community;

import com.island.community.CommunityApplication;
import com.island.community.dao.DiscussPostMapper;
import com.island.community.dao.LoginTicketMapper;
import com.island.community.entity.DiscussPost;
import com.island.community.service.DiscussPostService;
import com.island.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.Date;
import java.util.List;

@ContextConfiguration(classes= CommunityApplication.class)
@MapperScan("com.island.community.dao")
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testTextMail(){
        mailClient.sendMail("892340809@qq.com","Test","hello Lijiale");
    }

    @Test
    public void testHtmlMail()
    {
        Context context = new Context();
        context.setVariable("username","sunday");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("892340809@qq.com","HTML",content);
    }
    @Test
    public void stringReplace(){
        Date date=new Date(System.currentTimeMillis());
        System.out.println(date);
        date=new Date(System.currentTimeMillis()+1000*60*60*10);
        System.out.println(date);
    }

}