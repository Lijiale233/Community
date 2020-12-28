package com.island.community.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
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

    //调用者给予参数后 经过此方法变为json对象，转化为json格式字符串
    public static String getJSONString(int code, String msg, Map<String , Object> map){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(map!=null){
            for(String key: map.keySet()){
                jsonObject.put(key,map.get(key));
            }
        }

        return jsonObject.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    public static String getJSONString(int code){
        return getJSONString(code,null,null);
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",25);
        String json = getJSONString(1,"测试",map);
        System.out.println(json);
    }

}
