package com.island.community;

import com.island.community.dao.LoginTicketMapper;
import com.island.community.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
@MapperScan("com.island.community.dao")
class IslandApplicationTests {

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc","1");
        loginTicket=loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

}
