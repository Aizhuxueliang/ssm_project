package com.example.mapper;

import java.util.List;

import com.example.pojo.User;


public interface UserMapper {

    public List<User> getAllStu();

    public User getById(int id);


    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}