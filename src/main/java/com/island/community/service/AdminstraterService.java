package com.island.community.service;

import com.island.community.dao.AdminstraterMapper;
import com.island.community.dao.LoginTicketMapper;
import com.island.community.entity.Adminstrater;
import com.island.community.entity.LoginTicket;
import com.island.community.entity.User;
import com.island.community.util.CommunityConstant;
import com.island.community.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.*;

@Component
public class AdminstraterService {

    @Autowired
    private AdminstraterMapper adminstraterMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    public List<Adminstrater> findAllAdminstrater(){
         return  adminstraterMapper.selectAll();
    }

    public int insertAdminstrater(Adminstrater adminstrater){
        return adminstraterMapper.insertAdminstrater(adminstrater);
    }

    public int updatePassword(int id,String password){
        return adminstraterMapper.updatePassword(id,password);
    }

    public Map<String ,Object> registerAsAdminstrater(Adminstrater  adminstrater,String adminstraterCode){
        Map<String,Object> map =new HashMap<>();

        //判断空值
        if(adminstrater==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(adminstrater.getUsername())){
            map.put("usernameMsg","账号不能为空");
            System.out.println("账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(adminstrater.getPassword())){
            map.put("passwordMsg","密码不能为空 ");
            System.out.println("存在问题并返回问题");
            return map;
        }

        if(adminstraterCode.equals(CommunityConstant.ADMINSTRATER_CODE)){
            map.put("codeMsg","邀请码错误");
            System.out.println(adminstraterCode);
            System.out.println(CommunityConstant.ADMINSTRATER_CODE);
            System.out.println("检测到邀请码错误");
            return map;
        }

        adminstrater.setSalt(CommunityUtil.generateUUID().substring(0,5));
        adminstrater.setPassword(CommunityUtil.md5(adminstrater.getPassword()+adminstrater.getSalt()));
        adminstrater.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(200)));
        adminstraterMapper.insertAdminstrater(adminstrater);


        return map;
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
        Adminstrater adminstrater=adminstraterMapper.selectByName(username);
        if(adminstrater==null){
            map.put("usernameMsg","账号不存在");
            return map;
        }


        //验证密码
        password=CommunityUtil.md5(password+adminstrater.getSalt());
        if(!adminstrater.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }

        //验证通过，生成登陆凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(adminstrater.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setType(1);
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*expiredSeconds));

        loginTicketMapper.insertLoginTicket(loginTicket);

        //将已登陆用户的用户凭证返回给浏览器，以此记录已登陆的用户
        map.put("ticket",loginTicket.getTicket());

        return map;
    }
}
