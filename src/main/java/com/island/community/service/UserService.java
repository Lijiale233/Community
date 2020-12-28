package com.island.community.service;

import com.island.community.dao.LoginTicketMapper;
import com.island.community.dao.UserMapper;
import com.island.community.entity.LoginTicket;
import com.island.community.entity.User;
import com.island.community.util.CommunityConstant;
import com.island.community.util.CommunityUtil;
import com.island.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.*;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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
            System.out.println("账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空 ");
            System.out.println("存在问题并返回问题");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空 ");
            System.out.println("存在问题并返回问题");
            return map;

        }

        //验证账号
        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","账号已存在");
            System.out.println("存在问题并返回问题");
            return map;
        }
        //邮箱验证
        u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已被注册");
            System.out.println("存在问题并返回问题");
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//得到一个加密密钥
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt())); //加密密码
        user.setType(0);
        user.setStatus(0); //未激活
        user.setActivationCode(CommunityUtil.generateUUID());//设置激活码

        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(200)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());

        //规定注册邮件的路径
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        System.out.println(url);
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    public int activation(int userId,String code){
        User user =userMapper.selectById(userId);
        if(user.getStatus()==1)
            return ACTIVATION_REPEAT;
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else
            return ACTIVATION_FAILURE;
    }

    public Map<String,Object> login(String username,String password,int expiredSeconds)
    {
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        else if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //验证账号
        User user=userMapper.selectByName(username);
        if(user==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }

        //验证状态
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活");
            return map;
        }

        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //验证通过，生成登陆凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setType(0);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));

        loginTicketMapper.insertLoginTicket(loginTicket);

        //将已登陆用户的用户凭证返回给浏览器，以此记录已登陆的用户
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        ticket=ticket.replaceAll("/"," ");
        System.out.println(ticket);
        loginTicketMapper.updateStatus("1",ticket);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    //更新用户头像
    public int updateHeader(int userId, String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }


}
