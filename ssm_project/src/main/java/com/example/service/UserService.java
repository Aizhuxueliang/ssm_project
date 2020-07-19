package com.example.service;

import java.util.List;

import com.example.pojo.User;

public interface UserService {
    public List<User> getAllStu();

    void insert(User user);

    User selectByPrimaryKey(Integer id);

    void updateByPrimaryKeySelective(User user);

    void deleteByPrimaryKey(Integer id);
}