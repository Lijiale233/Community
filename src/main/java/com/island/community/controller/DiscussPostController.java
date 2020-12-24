package com.island.community.controller;

import com.island.community.dao.DiscussPostMapper;
import com.island.community.entity.Comment;
import com.island.community.entity.DiscussPost;
import com.island.community.entity.Page;
import com.island.community.entity.User;
import com.island.community.service.CommentService;
import com.island.community.service.DiscussPostService;
import com.island.community.service.UserService;
import com.island.community.util.CommunityConstant;
import com.island.community.util.CommunityUtil;
import com.island.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.FileNotFoundException;
import java.util.*;

//该类用于和社区的帖子数据交互
@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {

    //注入保持有用户信息的hostholder
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    public String addPost(String title,String content) {
        System.out.println("valid");
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "请先登录");
        }
        DiscussPost discussPost = new DiscussPost();

        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        System.out.println(discussPostService.addDiscussPost(discussPost));

        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String discussPostDetail(@PathVariable("discussPostId") int discussPostId, Model model) {

        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        if(discussPost==null){
            throw new IllegalArgumentException("未找到此条帖子的详细信息");
        }
        User user = userService.findById(discussPost.getUserId());
        model.addAttribute("post",discussPost);
        model.addAttribute("user", user);

        Page page = new Page();
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(discussPost.getCommentCount());


        List<Comment> commentList = new ArrayList<>();


        List<Map<String ,Object>> commentVoList = new ArrayList<>();
        commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());


        for(Comment comment : commentList){
            user=userService.findById(comment.getUserId());
            Map<String,Object> commentVo = new HashMap<>(); //一定要在每次循环开始时候重新声明，若在循环外声明，则commentVO会覆盖之前的不会保存多个，因为map的string值相同
            commentVo.put("user",user);
            commentVo.put("comment",comment);
            System.out.println(comment.getContent());

            List<Comment> replyList=commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),page.getOffset(),page.getLimit());
            List<Map<String,Object>>replyVoList=new ArrayList<>();
            if(replyList!=null){
                for(Comment reply:replyList) {
                    Map<String, Object> replyVo = new HashMap<>();
                    user=userService.findById(reply.getUserId());
                    replyVo.put("user",user);
                    replyVo.put("reply",reply);

                    User target = reply.getTargetId()==0?null:userService.findById(reply.getTargetId());
                    replyVo.put("target",target);
                    replyVoList.add(replyVo);
                }
            }
            commentVo.put("replys",replyVoList);

            int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
            commentVo.put("replyCount",replyCount);
            commentVoList.add(commentVo);
        }

        model.addAttribute("comments",commentVoList);
        System.out.println(commentVoList);
        model.addAttribute("page",page);
        return "/site/discuss-detail";
    }

}
