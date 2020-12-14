package com.island.community.util;

import com.island.community.entity.User;
import org.springframework.stereotype.Component;

//起到容器作用，用于代替session对象保存用户的信息，同时达到线程隔离的需求
@Component
public class HostHolder {

    private ThreadLocal<User> users=new ThreadLocal<>();

    public void setUsers(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }
}
