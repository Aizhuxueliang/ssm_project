package com.example.service.impl;

import java.util.HashMap;
import java.util.List;

import com.example.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryUserPage(Integer startRows) {
        return userMapper.queryUserPage(startRows);
    }

    @Override
    public List<User> selectUserPage(String userName, String userSex, Integer startRows) {
        return userMapper.selectUserPage(userName, userSex, startRows);
    }

    @Override
    public Integer getRowCount(String userName, String userSex) {
        return userMapper.getRowCount(userName, userSex);
    }

    @Override
    public Integer createUser(User user) {
        return userMapper.createUser(user);
    }

    @Override
    public Integer deleteUserById(String userId) {
        return userMapper.deleteUserById(userId);
    }

    @Override
    public Integer updateUserById(User user) {
        return userMapper.updateUserById(user);
    }


}