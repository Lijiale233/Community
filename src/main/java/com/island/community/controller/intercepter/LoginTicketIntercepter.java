package com.island.community.controller.intercepter;

import com.island.community.entity.LoginTicket;
import com.island.community.entity.User;
import com.island.community.service.UserService;
import com.island.community.util.CookieUtil;
import com.island.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketIntercepter implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override //参数不允许修改，应从request中得到cookie
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从cookie中获取凭证
        String ticket = CookieUtil.getValue(request,"ticket");//cookie的名字是ticket 其存放的value是目前用户的凭证

        if(ticket!=null){
            ticket=ticket.replaceAll("/"," ");
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查片凭证是否有效
            if(loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date())){
                //通过userticket拿到用户的user信息
                User user=userService.findById((loginTicket.getUserId()));
                //需要暂存已经登陆的user信息，要在多个线程中隔离存储user信息
                hostHolder.setUsers(user);
            }
        }
        return true;
    }

    //需要使用user的时候是在模版引擎调用，通过model表现在表现层上
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null&&modelAndView!=null){
            System.out.println(user);
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
