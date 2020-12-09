package com.island.community.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串作为激活码
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-"," ");
    }

    //对密码进行加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());//返回加密结果
    }


}
