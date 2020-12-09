package com.island.community.service;

import com.island.community.dao.UserMapper;
import com.island.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findById(int userId)
    {
        return userMapper.selectById(userId);
    }
}
