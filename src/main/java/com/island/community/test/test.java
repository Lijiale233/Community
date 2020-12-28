package com.island.community.test;

import com.island.community.CommunityApplication;
import com.island.community.dao.AdminstraterMapper;
import com.island.community.dao.DiscussPostMapper;
import com.island.community.entity.Adminstrater;
import com.island.community.entity.DiscussPost;
import com.island.community.entity.User;
import com.island.community.service.DiscussPostService;
import com.island.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes= CommunityApplication.class)
@MapperScan("com.island.community.dao")
public class test {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminstraterMapper adminstraterMapper;

    @Test
    public void testService()
     {
         List<DiscussPost>list= discussPostService.findDiscussPosts(0,0,10);
         for(DiscussPost post:list){
             System.out.println(post);
         }
     }

     @Test
    public void testUser(){
         User user=userService.findById(145);
         System.out.println(user);
     }

     @Test
    public void testAdminstraterMappeer(){
         Adminstrater adminstrater=new Adminstrater();
         adminstrater.setHeaderUrl("123");
         adminstrater.setUsername("lijiale");
         adminstrater.setPassword("123");
         adminstrater.setSalt("123123123");

         adminstraterMapper.insertAdminstrater(adminstrater);
         System.out.println(adminstraterMapper.selectAll());
     }






//    @Test
//    public void testSelectPosts(){
//       List<DiscussPost>list= discussPostMapper.selectDiscussPosts(0,0,10);
//       for(DiscussPost post:list){
//           System.out.println(post);
//       }
//    }
}
