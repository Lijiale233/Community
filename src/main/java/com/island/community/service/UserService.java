package com.island.community.service;

import com.island.community.dao.UserMapper;
import com.island.community.entity.User;
import com.island.community.util.CommunityUtil;
import com.island.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findById(int userId)
    {
        return userMapper.selectById(userId);
    }

    public Map<String ,Object> register(User user){
        Map<String,Object> map =new HashMap<>();

        //判断空值
        if(user==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空 ");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空 ");
            return map;

        }

        //验证账号
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","账号已存在");
            return map;
        }
        //邮箱验证
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已被注册");
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID());
        return map;
    }
}
