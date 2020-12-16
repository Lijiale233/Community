package com.island.community.controller;

import com.island.community.annotation.LoginRequired;
import com.island.community.entity.User;
import com.island.community.service.UserService;
import com.island.community.util.CommunityUtil;
import com.island.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
@RequestMapping(path = "/user")
public class UserController {

    @Value("${community.path.upload}")
    private  String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    //跳转至修改页面
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "site/setting";
    }

    //上传图片
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片上传");
            return "site/setting";
        }
        else{
            //注意对上传文件重新命名，防止用户上传的文件命名冲突
            String fileName = headerImage.getOriginalFilename();
            //获取文件的后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));

            if(StringUtils.isBlank(suffix)){ //如果后缀为空
                model.addAttribute("error","文件格式不正确");
                return "site/setting";
            }
            else{
                //拼接随机名称与后缀
                fileName = CommunityUtil.generateUUID()+suffix;
                //确定文件的存放路径（服务器中）
                File dest = new File(uploadPath+"/"+fileName);
                try {
                    headerImage.transferTo(dest);
                } catch (IOException e) {
                   logger.error("上传文件失败");
                   throw new RuntimeException("上传文件失败,服务器发生异常" ,e);
                }

                //更新用户头像的路径（web访问路径）
                //http://localhost/community/user/header/xxx.png

                //从目前线程中取得用户信息
                User user=hostHolder.getUser();
                //设置新的头像路径（由新的congtroller去访问此路径的头像）
                String headerUrl=domain+contextPath+"/user"+"/header/"+fileName;
                userService.updateHeader(user.getId(),headerUrl);
                return "redirect:/index";
            }
        }
    }

    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName")String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath+"/"+fileName;
        //文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType(("image/"+suffix));

        try(
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os=response.getOutputStream();
                ) {
            byte[] buffer = new byte[1024];
            int b=0;
            while ((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }
}
