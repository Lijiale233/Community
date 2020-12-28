package com.island.community.util;

public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS=0;

    //重复激活
    int ACTIVATION_REPEAT=1;

    //激活失败
    int ACTIVATION_FAILURE=2;

    //默认超时时间
    int DEFAULT_EXPIRED_SECONDS=3600 * 12;

    //记住状态下超时时间
    int REMEMBER_EXPIRED_SECONDS=3600 * 24 *100;

    //评论对象为帖子
    int ENTITY_TYPE_POST=1;

    //评论类型为评论
    int ENTITY_TYPE_COMMENT=2;

    //管理员邀请码
    String ADMINSTRATER_CODE="islandisthebest";


}
