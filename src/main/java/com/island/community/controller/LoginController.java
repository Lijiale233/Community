package com.island.community.controller;

import com.google.code.kaptcha.Producer;
import com.island.community.entity.Adminstrater;
import com.island.community.entity.User;
import com.island.community.service.AdminstraterService;
import com.island.community.service.UserService;
import com.island.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private AdminstraterService adminstraterService;

    @Value("${server.servlet.context-path}")
    private String contextPath;


    @RequestMapping(path = "/registerAsUser",method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String,Object> map = userService.register(user);
        if(map==null||map.isEmpty())
        {
            model.addAttribute("msg","注册成功，我们已向您邮箱发送了一封激活邮件，请查收");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/registerAsAdminstrater",method = RequestMethod.POST)
    public String registerAsAdminstrater(Model model, Adminstrater adminstrater, @RequestParam("adminstraterCode") String adminstraterCode){
        System.out.println("code is " +adminstraterCode);
        System.out.println("get username "+adminstrater.getUsername());
        Map<String,Object> map = adminstraterService.registerAsAdminstrater(adminstrater,adminstraterCode);
        if(map==null||map.isEmpty())
        {
            model.addAttribute("msg","管理员注册成功，将直接跳转至主页面");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else
        {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        int result=userService.activation(userId,code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        } else {
            System.out.println(result);
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //方法返回图片所以要使用response 向浏览器返回验证码图片
    //因为涉及验证码的验证，对此敏感信息需要跨服务传输，因此利用session对象
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text=kaptchaProducer.createText();
        BufferedImage image=kaptchaProducer.createImage(text);

        //存储验证码答案
        session.setAttribute("kaptcha",text);

        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //请求路径可以重复只要请求方法有区别就行
    @RequestMapping(path="/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,
                        boolean rememberMe,Model model,HttpSession session,HttpServletResponse response){

        //检查验证码 不涉及数据层交互直接在表现层解决该问题
        String kaptcher = (String)session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcher)||StringUtils.isBlank(code)||!kaptcher.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }

        //检查账号，密码
        int expiredSeconds = rememberMe?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map=userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString().replaceAll(" ","/"));
            System.out.println(map.get("ticket").toString().replaceAll(" ","/"));
            cookie.setPath(contextPath);//cookie信息应该应用于整个项目中
            System.out.println(contextPath);
            System.out.println(expiredSeconds);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);//将cookie发送给浏览器
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("codeMsg",map.get("codeMsg"));
            return "/site/login";
        }
    }

    @RequestMapping(path="/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket")String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }


}
