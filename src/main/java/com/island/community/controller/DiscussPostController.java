package com.island.community.controller;

import com.island.community.dao.DiscussPostMapper;
import com.island.community.entity.DiscussPost;
import com.island.community.entity.User;
import com.island.community.service.DiscussPostService;
import com.island.community.util.CommunityUtil;
import com.island.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DiscussPostController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    public String addPost(String title,String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"请先登录");
        }
        DiscussPost discussPost = new DiscussPost();

        discussPost.setId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0, "发布成功!");
    }

}
